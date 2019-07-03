package com.example.policyfolio.UI.Claim;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.policyfolio.Data.Local.Classes.Notifications;
import com.example.policyfolio.UI.Document.DocumentActivity;
import com.example.policyfolio.UI.Help.HelpActivity;
import com.example.policyfolio.UI.LoginSignUp.LoginSignUpActivity;
import com.example.policyfolio.UI.Nominee.NomineeSupportActivity;
import com.example.policyfolio.UI.Promotions.PromotionsActivity;
import com.example.policyfolio.UI.AddPolicy.AddPolicyActivity;
import com.example.policyfolio.UI.Base.BaseNavigationActivity;
import com.example.policyfolio.UI.Base.ParentChildNavigationCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Util.Receivers.PremiumDuesReceiver;
import com.example.policyfolio.ViewModels.WithUser.ClaimViewModel;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;

import com.example.policyfolio.R;

import java.util.List;

public class ClaimSupportActivity extends BaseNavigationActivity implements ClaimCallback, ParentChildNavigationCallback {

    private ClaimViewModel viewModel;

    private ClaimDashboardFragment claimDashboardFragment;
    private TrackClaimFragment trackClaimFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        super.setCallback(this);
        super.setMenuSelection(R.id.claim_support);
        getSupportActionBar().setTitle("Claims");

        viewModel = ViewModelProviders.of(this).get(ClaimViewModel.class);
        viewModel.initiateRepo(this);

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
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        intent.putExtra(Constants.Query.TYPE,Constants.Query.Type.CLAIMS);
        startActivityForResult(intent,Constants.PermissionAndRequests.HELP_REQUEST);
    }

    @Override
    public void legalSupport() {
        showSnackbar("Under Construction");
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
            showSnackbar("Unable to Process Call");
        }
    }

    @Override
    public void addPolicy() {
        Intent intent = new Intent(this, AddPolicyActivity.class);
        startActivityForResult(intent, Constants.PermissionAndRequests.ADD_POLICY_REQUEST);
        finish();
    }

    @Override
    public void documentVault() {
        Intent intent = new Intent(this, DocumentActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.DOCUMENTS_REQUEST);
        finish();
    }

    @Override
    public void promotions() {
        Intent intent = new Intent(this, PromotionsActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.PROMOTIONS_REQUEST);
        finish();
    }

    @Override
    public void claimSupport() {

    }

    @Override
    public void getHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.HELP_REQUEST);
        finish();
    }

    @Override
    public void nomineeDashboard() {
        Intent intent = new Intent(this, NomineeSupportActivity.class);
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

                    Intent intent = new Intent(ClaimSupportActivity.this, LoginSignUpActivity.class);
                    intent.putExtra(Constants.LoginInInfo.POST_LOGOUT,true);
                    startActivity(intent);
                    finish();
                }
                else {
                    showSnackbar("LogOut Failed");
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
}
