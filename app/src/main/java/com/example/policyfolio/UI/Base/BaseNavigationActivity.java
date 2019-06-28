package com.example.policyfolio.UI.Base;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.policyfolio.R;
import com.example.policyfolio.Util.Constants;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

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
        toolbar = findViewById(R.id.toolbar);
        if(toolbar!=null)
            setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        name = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.nav_name);
        navigationView = findViewById(R.id.nav_view);

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
                if(inProgress()) {
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

    protected void setMenuSelection(int id) {
        onNavigationItemSelected(navigationView.getMenu().findItem(id));
    }
}
