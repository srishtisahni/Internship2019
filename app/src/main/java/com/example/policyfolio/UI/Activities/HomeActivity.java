package com.example.policyfolio.UI.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Repo.Database.DataClasses.Policy;
import com.example.policyfolio.Repo.Database.DataClasses.User;
import com.example.policyfolio.R;
import com.example.policyfolio.UI.Activities.NavigationActionActivities.AddPolicyActivity;
import com.example.policyfolio.Util.CallBackListeners.HomeCallback;
import com.example.policyfolio.UI.Fragments.HomePoliciesFragment;
import com.example.policyfolio.UI.Fragments.HomeStartupFragment;
import com.example.policyfolio.ViewModels.HomeViewModel;
import com.google.android.material.navigation.NavigationView;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeCallback {

    private HomeViewModel viewModel;

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private FrameLayout snackBar;
    private NavigationView navigationView;

    private TextView name;

    private HomeStartupFragment homeStartupFragment;
    private HomePoliciesFragment homePoliciesFragment;

    private Toast timeToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);
        snackBar = findViewById(R.id.snackbar_action);
        drawer = findViewById(R.id.drawer_layout);
        name = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.nav_name);
        navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PolicyFolio");

        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        viewModel.initiateRepo(this);

        setUpDrawer();
        fetchInfo();
        getUser();
    }

    private void getUser() {
        viewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                fragmentHolder.setAlpha(1f);
                progressBar.setVisibility(View.GONE);
                if(user!=null){
                    getTimeToast(user.getLastUpdated());
                    if(user.getName()!=null){
                        getSupportActionBar().setTitle(user.getFirstName() + "'s Profile");
                        name.setText(user.getFirstName());
                    }
                    if(!user.isComplete()){                                        //If user information is not complete, generate a pop up to fetch information
                        Intent intent = new Intent(HomeActivity.this,PopUpActivity.class);
                        intent.putExtra(Constants.PopUps.POPUP_TYPE,Constants.PopUps.Type.INFO_POPUP);
                        intent.putExtra(Constants.LoginInInfo.FIREBASE_UID,viewModel.getUid());
                        startActivityForResult(intent, Constants.PermissionAndRequests.UPDATE_REQUEST);
                    }
                    else{
                        renderFragment();                                       //Render UI based on the user info
                    }
                }
            }
        });
    }

    private void getTimeToast(Long lastUpdated) {
        if(timeToast!=null)
            timeToast.cancel();
        Long timeDiffernce = System.currentTimeMillis() - lastUpdated;
        int sec = (int) (timeDiffernce/1000);
        int minutes = (int) (timeDiffernce/(1000*60));
        int hours = (int) (timeDiffernce/(1000*60*60));
        int days = (int) (timeDiffernce/(1000*60*60*24));
        String time = "";
        if(days!=0)
            time = time + days + " days ";
        if(hours!=0)
            time = time + hours + " hours";
        if(minutes!=0)
            time = time + minutes + " mins ";
        if(sec!=0)
            time = time + sec + " secs ";
        if(time.length()>0) {
            time = time + "ago";
            timeToast = Toast.makeText(this,"Updated "+time,Toast.LENGTH_SHORT);
        }
        else {
            timeToast = null;
        }
        if(timeToast!=null)
            timeToast.show();
    }

    private void fetchInfo() {
        Bundle bundle = getIntent().getExtras();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE);
        if(bundle.getBoolean(Constants.LoginInInfo.LOGGED_IN)){                     //Update the shared preference with the latest information
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.LoginInInfo.LOGGED_IN,true);
            editor.putInt(Constants.LoginInInfo.TYPE,bundle.getInt(Constants.LoginInInfo.TYPE));
            editor.putString(Constants.LoginInInfo.FIREBASE_UID,bundle.getString(Constants.LoginInInfo.FIREBASE_UID));
            editor.apply();
            viewModel.setType(bundle.getInt(Constants.LoginInInfo.TYPE));
            viewModel.setUid(bundle.getString(Constants.LoginInInfo.FIREBASE_UID));
        }
    }

    private void renderFragment() {
        fragmentHolder.setAlpha(.4f);
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getPolicies().observe(this, new Observer<List<Policy>>() {
            @Override
            public void onChanged(List<Policy> policies) {
                fragmentHolder.setAlpha(1f);
                progressBar.setVisibility(View.GONE);
                if(policies!=null){                                                                 //Rendering multiple policy fragment
                    if(policies.size()==0)                                                          //If no policies are added yet, render zero policy fragment
                        zeroPolicies();
                    else {
                        Long time = Long.valueOf(0);
                        for(int i=0;i<policies.size();i++){
                            if(time < policies.get(i).getLastUpdated())
                                time = policies.get(i).getLastUpdated();
                        }
                        getTimeToast(time);
                        policies();
                    }
                }
            }
        });
    }

    private void policies() {
        if(homePoliciesFragment==null) {
            homePoliciesFragment = new HomePoliciesFragment(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, homePoliciesFragment).commit();
        }
    }

    private void zeroPolicies() {
        homeStartupFragment = new HomeStartupFragment(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, homeStartupFragment).commit();
    }


    @Override
    public void addPolicy() {
        Intent intent = new Intent(this, AddPolicyActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        startActivityForResult(intent,Constants.PermissionAndRequests.ADD_POLICY_REQUEST);
    }

    @Override
    public void addPolicy(int type) {
        Intent intent = new Intent(this, AddPolicyActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        intent.putExtra(Constants.InsuranceProviders.TYPE,type);
        startActivityForResult(intent,Constants.PermissionAndRequests.ADD_POLICY_REQUEST);
    }

    private void setUpDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.home_icon, this.getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.add_policy:
                item.setChecked(false);
                addPolicy();
                break;
            case R.id.logout:
                item.setChecked(false);
                logOut();
                break;
            case R.id.nominee_support:

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        viewModel.logOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();

                    Intent intent = new Intent(HomeActivity.this,LoginSignUpActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(HomeActivity.this,"LogOut Failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.PermissionAndRequests.UPDATE_REQUEST && resultCode == Constants.PermissionAndRequests.UPDATE_RESULT){
//            renderFragment();                                       //Render Fragments based on user information
        }
        if(requestCode == Constants.PermissionAndRequests.ADD_POLICY_REQUEST && resultCode == Constants.PermissionAndRequests.ADD_POLICY_RESULT){
//            policies();
        }
    }

}
