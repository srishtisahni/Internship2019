package com.example.policyfolio.ui.base;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.policyfolio.R;
import com.example.policyfolio.util.Constants;
import com.google.android.material.navigation.NavigationView;

public class BaseNavigationActivity extends BasicProgressActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView name;

    private static String nameText;
    private ParentChildNavigationCallback callback;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        super.whiteNavigation();
        toolbar = findViewById(R.id.toolbar);
        if(toolbar!=null)
            setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        drawer = findViewById(R.id.drawer_layout);
        name = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.nav_name);
        navigationView = findViewById(R.id.nav_view);

        setUpDrawer();
    }

    private void setUpDrawer() {
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.home_icon, this.getTheme());
        mDrawerToggle.setHomeAsUpIndicator(drawable);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inProgress() && !isSheetOpen()) {
                    if (drawer.isDrawerVisible(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            }
        });
        mDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void updateName() {
        if(nameText != null)
            name.setText(nameText);
    }

    protected static void setNameText(String nameText) {
        BaseNavigationActivity.nameText = nameText;
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

    protected boolean isDrawerOpen() {
        return drawer.isDrawerOpen(GravityCompat.START);
    }

    protected void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    protected void setMenuSelection(int id) {
        navigationView.setCheckedItem(id);
    }

    protected void removeAllSelections() {
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Constants.PermissionAndRequests.ADD_POLICY_REQUEST:
                break;
            case Constants.PermissionAndRequests.HELP_REQUEST:
                if(resultCode == Constants.PermissionAndRequests.HELP_RESULT)
                    showSnackbar("We will get back on your query as soon as possible");
                break;
        }
    }
}
