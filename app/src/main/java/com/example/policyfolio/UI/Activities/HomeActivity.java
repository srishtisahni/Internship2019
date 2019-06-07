package com.example.policyfolio.UI.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.Policy;
import com.example.policyfolio.DataClasses.User;
import com.example.policyfolio.R;
import com.example.policyfolio.UI.CallBackListeners.HomeCallback;
import com.example.policyfolio.UI.Fragments.HomeStartupFragment;
import com.example.policyfolio.ViewModels.HomeViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

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

    private HomeStartupFragment policyStartupFragment;

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

        viewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if(user.getName()!=null){
                    fragmentHolder.setAlpha(1f);
                    progressBar.setVisibility(View.GONE);
                    getSupportActionBar().setTitle(user.getFirstName() + "'s Profile");
                    name.setText(user.getFirstName());
                }
            }
        });

        setUpDrawer();

        fetchInfo();
    }

    private void fetchInfo() {
        Bundle bundle = getIntent().getExtras();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE);
        if(bundle.getBoolean(Constants.LoginInInfo.LOGGED_IN)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.LoginInInfo.LOGGED_IN,true);
            editor.putInt(Constants.LoginInInfo.TYPE,bundle.getInt(Constants.LoginInInfo.TYPE));
            editor.putString(Constants.LoginInInfo.FIREBASE_UID,bundle.getString(Constants.LoginInInfo.FIREBASE_UID));
            editor.commit();
            viewModel.setType(bundle.getInt(Constants.LoginInInfo.TYPE));
            viewModel.setUid(bundle.getString(Constants.LoginInInfo.FIREBASE_UID));
        }
        viewModel.fetchUser(this).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if(user!=null){
                    Log.e("COMPLETE",user.isComplete()+"");
                    if(!user.isComplete()){
                        Intent intent = new Intent(HomeActivity.this,PopUpActivity.class);
                        intent.putExtra(Constants.PopUps.POPUP_TYPE,Constants.PopUps.Type.INFO_POPUP);
                        intent.putExtra(Constants.LoginInInfo.FIREBASE_UID,viewModel.getUid());
                        startActivityForResult(intent,Constants.FirebaseDataManagement.UPDATE_REQUEST);
                    }
                    else{
                        viewModel.updateUser(user);
                        renderFragment(user);
                    }
                }
                else {
                    Toast.makeText(HomeActivity.this,"Fetching Error Occurred",Toast.LENGTH_SHORT).show();
                    Snackbar.make(snackBar,"Please Check you Internet Connection and Retry",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void renderFragment(User user) {
        viewModel.fetchPolicies(user.getId(),this).observe(this, new Observer<List<Policy>>() {
            @Override
            public void onChanged(List<Policy> policies) {
                if(policies!=null){
                    if(policies.size()==0)
                        zeroPolicies();
                    else
                        policies();
                }
                else {
                    Toast.makeText(HomeActivity.this,"Fetching Error Occurred",Toast.LENGTH_SHORT).show();
                    Snackbar.make(snackBar,"Please Check you Internet Connection and Retry",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void policies() {
    }

    private void zeroPolicies() {
        policyStartupFragment = new HomeStartupFragment(this);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,policyStartupFragment).commit();
    }


    @Override
    public void addPolicy() {
        Intent intent = new Intent(this,AddPolicyActivity.class);
        startActivity(intent);
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
                addPolicy();
                break;
        }
        navigationView.getCheckedItem().setChecked(false);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.FirebaseDataManagement.UPDATE_REQUEST && resultCode == Constants.FirebaseDataManagement.UPDATE_RESULT){
            viewModel.updateUser((User) data.getParcelableExtra(Constants.User.USER));
            renderFragment((User) data.getParcelableExtra(Constants.User.USER));
        }
    }

}
