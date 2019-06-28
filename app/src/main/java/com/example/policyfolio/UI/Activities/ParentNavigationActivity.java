package com.example.policyfolio.UI.Activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.policyfolio.R;
import com.example.policyfolio.Util.CallBackListeners.ParentChildNavigationCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class ParentNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView name;
    private FrameLayout snackbar;

    private static String nameText;
    private ParentChildNavigationCallback callback;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        toolbar = findViewById(R.id.toolbar);
        if(toolbar!=null)
            setSupportActionBar(toolbar);
        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);
        drawer = findViewById(R.id.drawer_layout);
        name = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.nav_name);
        navigationView = findViewById(R.id.nav_view);
        snackbar = findViewById(R.id.snackbar_action);

        setUpDrawer();
        updateName();
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
                if(progressBar.getVisibility() == View.GONE) {
                    if (drawer.isDrawerVisible(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            }
        });

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void updateName() {
        if(nameText != null)
            name.setText(nameText);
    }

    protected static void setNameText(String nameText) {
        ParentNavigationActivity.nameText = nameText;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.add_policy:
                callback.addPolicy();
                break;
            case R.id.logout:
                callback.logOut();
                break;
            case R.id.nominee_support:
                callback.nomineeDashboard();
                break;
            case R.id.help:
                callback.getHelp();
                break;
            case R.id.promotions:
                callback.promotions();
                break;
            case R.id.claim_support:
                callback.claimSupport();
                break;
            case R.id.documents:
                callback.documentVault();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void setCallback(ParentChildNavigationCallback callback) {
        this.callback = callback;
    }

    protected void startProgress(){
        fragmentHolder.setAlpha(0.4f);
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void endProgress() {
        fragmentHolder.setAlpha(1f);
        progressBar.setVisibility(View.GONE);
    }

    protected boolean inProgress(){
        return progressBar.getVisibility() == View.VISIBLE;
    }

    protected boolean isDrawerOpen() {
        return drawer.isDrawerOpen(GravityCompat.START);
    }

    protected void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }
}
