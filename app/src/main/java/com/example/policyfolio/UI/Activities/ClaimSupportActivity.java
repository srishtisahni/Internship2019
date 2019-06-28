package com.example.policyfolio.UI.Activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.policyfolio.Repo.Database.DataClasses.Notifications;
import com.example.policyfolio.UI.Fragments.Claim.OnGoingFragment;
import com.example.policyfolio.UI.Fragments.Claim.ResolvedFragment;
import com.example.policyfolio.UI.Fragments.Claim.ClaimDashboardFragment;
import com.example.policyfolio.UI.Fragments.Claim.TrackClaimFragment;
import com.example.policyfolio.Util.CallBackListeners.ClaimCallback;
import com.example.policyfolio.Util.CallBackListeners.ParentChildNavigationCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Util.Receivers.PremiumDuesReceiver;
import com.example.policyfolio.ViewModels.ClaimViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.policyfolio.R;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class ClaimSupportActivity extends ParentNavigationActivity implements ClaimCallback, ParentChildNavigationCallback {

    private ClaimViewModel viewModel;

    private ClaimDashboardFragment claimDashboardFragment;
    private TrackClaimFragment trackClaimFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        getSupportActionBar().setTitle("Claims");

        viewModel = ViewModelProviders.of(this).get(ClaimViewModel.class);
        viewModel.initiateRepo(this);

        viewModel.setuId(getIntent().getStringExtra(Constants.User.ID));
        viewModel.setLoginType(getIntent().getIntExtra(Constants.User.LOGIN_TYPE,-1));

        super.setCallback(this);

        setUpDashboard();
    }

    private void setUpDashboard() {
        super.endProgress();
        if(claimDashboardFragment == null)
            claimDashboardFragment = new ClaimDashboardFragment(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,claimDashboardFragment).commit();
    }

    @Override
    public void claimAssistance() {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        intent.putExtra(Constants.Query.TYPE,Constants.Query.Type.CLAIMS);
        startActivityForResult(intent,Constants.PermissionAndRequests.HELP_REQUEST);
    }

    @Override
    public void legalSupport() {
        Toast.makeText(this,"Under Construction",Toast.LENGTH_LONG).show();
    }

    @Override
    public void trackClaim() {
        trackClaimFragment = new TrackClaimFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,trackClaimFragment).commit();
    }

    @Override
    public void callAssistance() {
        Uri u = Uri.parse("tel:" + Constants.Claims.NUMBER);
        Intent intent = new Intent(Intent.ACTION_DIAL, u);
        try
        {
            startActivity(intent);
        }
        catch (SecurityException s)
        {
            Toast.makeText(this, "Unable to Process Call", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.PermissionAndRequests.HELP_REQUEST && resultCode == Constants.PermissionAndRequests.HELP_RESULT)
            Toast.makeText(this,"We will get back on your query as soon as possible",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (super.isDrawerOpen()) {
            super.closeDrawer();
        } else {
            if(trackClaimFragment!=null){
                List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                for(int i=0;i<fragmentList.size();i++){
                    if(fragmentList.get(i) instanceof  TrackClaimFragment ||  fragmentList.get(i) instanceof OnGoingFragment || fragmentList.get(i) instanceof ResolvedFragment) {
                        getSupportFragmentManager().beginTransaction().remove(fragmentList.get(i)).commit();
                        trackClaimFragment = null;
                    }
                }
            }
            else
                super.onBackPressed();
        }
    }

    @Override
    public void addPolicy() {
        Intent intent = new Intent(this, AddPolicyActivity.class);
        intent.putExtra(Constants.User.ID, viewModel.getuId());
        startActivityForResult(intent, Constants.PermissionAndRequests.ADD_POLICY_REQUEST);
        finish();
    }

    @Override
    public void documentVault() {
        Intent intent = new Intent(this, DocumentActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        startActivityForResult(intent,Constants.PermissionAndRequests.DOCUMENTS_REQUEST);
        finish();
    }

    @Override
    public void promotions() {
        Intent intent = new Intent(this, PromotionsActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        startActivityForResult(intent,Constants.PermissionAndRequests.PROMOTIONS_REQUEST);
        finish();
    }

    @Override
    public void claimSupport() {

    }

    @Override
    public void getHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        startActivityForResult(intent,Constants.PermissionAndRequests.HELP_REQUEST);
        finish();
    }

    @Override
    public void nomineeDashboard() {
        Intent intent = new Intent(this, NomineeSupportActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        startActivityForResult(intent,Constants.PermissionAndRequests.NOMINEE_DASHBOARD_REQUEST);
        finish();
    }

    @Override
    public void logOut() {
        super.startProgress();
        cancelNotifications();
        viewModel.logOut().observe(ClaimSupportActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                ClaimSupportActivity.super.endProgress();
                if(aBoolean){
                    getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE).edit().clear().apply();
                    getSharedPreferences(Constants.Policy.UPDATED_SHARED_PREFRENCE,MODE_PRIVATE).edit().clear().apply();

                    Intent intent = new Intent(ClaimSupportActivity.this,LoginSignUpActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(ClaimSupportActivity.this,"LogOut Failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void cancelNotifications() {
        getSharedPreferences(Constants.Notification.NOTIFICATION_SHARED_PREFERENCE,MODE_PRIVATE).edit().clear().apply();
        viewModel.getAllNotificatios().observe(this, new Observer<List<Notifications>>() {
            @Override
            public void onChanged(List<Notifications> notifications) {
                if(!notifications.isEmpty()) {
                    for (int i = 0; i < notifications.size(); i++) {
                        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                        Intent intent = new Intent(ClaimSupportActivity.this, PremiumDuesReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(ClaimSupportActivity.this, (int) notifications.get(i).getId(), intent, PendingIntent.FLAG_NO_CREATE);
                        if (pendingIntent != null) {
                            alarm.cancel(pendingIntent);
                        }
                    }
                    viewModel.deleteAllNotifications();
                    Log.e("NOTIFICATIONS", "Cancelled");
                }
            }
        });
    }
}
