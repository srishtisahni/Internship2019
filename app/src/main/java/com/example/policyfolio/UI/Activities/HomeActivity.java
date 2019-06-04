package com.example.policyfolio.UI.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.User;
import com.example.policyfolio.R;
import com.example.policyfolio.ViewModels.HomeViewModel;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private HomeViewModel viewModel;
    private DrawerLayout drawer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("PolicyFolio");

        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        viewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if(user.getName()!=null){
                    getSupportActionBar().setTitle(user.getName() + "'s Profile");
                }
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        setUpDrawer();

        fetchInfo();

    }

    private void fetchInfo() {
        Bundle bundle = getIntent().getExtras();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE);
        if(bundle.getBoolean(Constants.SharedPreferenceKeys.LOGGED_IN)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.SharedPreferenceKeys.LOGGED_IN,true);
            editor.putInt(Constants.SharedPreferenceKeys.TYPE,bundle.getInt(Constants.SharedPreferenceKeys.TYPE));
            editor.putString(Constants.SharedPreferenceKeys.FIREBASE_UID,bundle.getString(Constants.SharedPreferenceKeys.FIREBASE_UID));
            editor.commit();
            viewModel.setType(bundle.getInt(Constants.SharedPreferenceKeys.TYPE));
            viewModel.setUid(bundle.getString(Constants.SharedPreferenceKeys.FIREBASE_UID));
        }
        viewModel.fetchUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if(user!=null){
                    viewModel.updateUser(user);
                    if(!user.isComplete()){
                        Intent intent = new Intent(HomeActivity.this,PopUpActivity.class);
                        intent.putExtra(Constants.PopUps.POPUP_TYPE,Constants.PopUps.Type.INFO_POPUP);
                        intent.putExtra(Constants.SharedPreferenceKeys.FIREBASE_UID,viewModel.getUid());
                        startActivityForResult(intent,Constants.FirebaseDataManagement.UPDATE_REQUEST);
                    }
                }
                else {
                    Toast.makeText(HomeActivity.this,"Fetching Error Occurred",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void setUpDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
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

        if (id == R.id.nav_home) {
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.FirebaseDataManagement.UPDATE_REQUEST && resultCode == Constants.FirebaseDataManagement.UPDATE_RESULT){
            viewModel.updateUser((User) data.getParcelableExtra(Constants.User.USER));
        }
    }
}
