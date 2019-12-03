package com.example.policyfolio.ui.empty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.policyfolio.R;
import com.example.policyfolio.data.local.classes.Notifications;
import com.example.policyfolio.ui.addpolicy.AddPolicyActivity;
import com.example.policyfolio.ui.base.BaseNavigationActivity;
import com.example.policyfolio.ui.base.ParentChildNavigationCallback;
import com.example.policyfolio.ui.claim.ClaimSupportActivity;
import com.example.policyfolio.ui.document.DocumentActivity;
import com.example.policyfolio.ui.help.HelpActivity;
import com.example.policyfolio.ui.login.LoginSignUpActivity;
import com.example.policyfolio.ui.nominee.NomineeSupportActivity;
import com.example.policyfolio.ui.promotions.PromotionsActivity;
import com.example.policyfolio.ui.promotions.PromotionsFragment;
import com.example.policyfolio.util.Constants;
import com.example.policyfolio.util.receivers.PremiumNotificationReceiver;
import com.example.policyfolio.viewmodels.EmptyViewModel;
import com.example.policyfolio.viewmodels.PromotionViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class EmptyActivity extends BaseNavigationActivity implements NavigationView.OnNavigationItemSelectedListener, ParentChildNavigationCallback {

    private EmptyViewModel viewModel;

    private PromotionsFragment promotionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        super.setCallback(this);
        super.setMenuSelection(R.id.promotions);
        getSupportActionBar().setTitle("Under Construction");

        viewModel = ViewModelProviders.of(this).get(EmptyViewModel.class);
        viewModel.initiateRepo(this);

        setUpFragment();
    }

    private void setUpFragment() {
        super.endProgress();
        if(promotionsFragment == null)
            promotionsFragment = new PromotionsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,promotionsFragment).commit();
    }

    @Override
    public void addPolicy() {
        Intent intent = new Intent(this, AddPolicyActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.ADD_POLICY_REQUEST);
        finish();
    }

    @Override
    public void documentVault() {
        Intent intent = new Intent(this, DocumentActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.DOCUMENTS_REQUEST);
        finish();
    }

    @Override
    public void home() {
        finish();
    }

    @Override
    public void optionUnavailable() {

    }

    @Override
    public void claimSupport() {
        Intent intent = new Intent(this, ClaimSupportActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.CLAIMS_REQUEST);
        finish();
    }

    @Override
    public void nomineeDashboard() {
        Intent intent = new Intent(this, NomineeSupportActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.NOMINEE_DASHBOARD_REQUEST);
        finish();
    }

    @Override
    public void getHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.HELP_REQUEST);
        finish();
    }

    @Override
    public void promotions() {
        Intent intent = new Intent(this, PromotionsActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.PROMOTIONS_REQUEST);
        finish();
    }

    @Override
    public void logOut() {
        super.startProgress();
        cancelNotifications();
        viewModel.logOut().observe(EmptyActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                EmptyActivity.super.endProgress();
                if(aBoolean){
                    getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE).edit().clear().apply();
                    getSharedPreferences(Constants.Policy.UPDATED_SHARED_PREFRENCE,MODE_PRIVATE).edit().clear().apply();

                    Intent intent = new Intent(EmptyActivity.this, LoginSignUpActivity.class);
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
                        Intent intent = new Intent(EmptyActivity.this, PremiumNotificationReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(EmptyActivity.this, (int) notifications.get(i).getId(), intent, PendingIntent.FLAG_NO_CREATE);
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
            super.onBackPressed();
        }
    }
}
