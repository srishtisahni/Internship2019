package com.example.policyfolio.UI.Help;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.example.policyfolio.Data.Local.Classes.Notifications;
import com.example.policyfolio.UI.LoginSignUp.LoginSignUpActivity;
import com.example.policyfolio.UI.Nominee.NomineeSupportActivity;
import com.example.policyfolio.UI.Promotions.PromotionsActivity;
import com.example.policyfolio.UI.AddPolicy.AddPolicyActivity;
import com.example.policyfolio.UI.Base.BaseNavigationActivity;
import com.example.policyfolio.UI.Claim.ClaimSupportActivity;
import com.example.policyfolio.UI.Document.DocumentActivity;
import com.example.policyfolio.UI.Base.ParentChildNavigationCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Util.Receivers.PremiumDuesReceiver;
import com.example.policyfolio.ViewModels.HelpViewModel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;

import com.example.policyfolio.R;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class HelpActivity extends BaseNavigationActivity implements NeedHelpCallback, NavigationView.OnNavigationItemSelectedListener, ParentChildNavigationCallback {

    private HelpViewModel viewModel;

    private NeedHelpFragment needHelpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        super.setCallback(this);
        super.setMenuSelection(R.id.help);

        getSupportActionBar().setTitle("Need Help");

        viewModel = ViewModelProviders.of(this).get(HelpViewModel.class);
        viewModel.initiateRepo(this);

        viewModel.setuId(getIntent().getStringExtra(Constants.User.ID));
        viewModel.setType(getIntent().getIntExtra(Constants.Query.TYPE,-1));
        viewModel.setLoginType(getIntent().getIntExtra(Constants.User.LOGIN_TYPE,-1));

        setUpFragment();
    }

    private void setUpFragment() {
        super.endProgress();
        if(needHelpFragment == null)
            needHelpFragment = new NeedHelpFragment(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,needHelpFragment).commit();
    }

    @Override
    public void save() {
        startProgress();
        viewModel.saveQuery().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                endProgress();
                if(s == null){
                    showSnackbar("Unable to update Information");
                    needHelpFragment.enableButton();
                }
                else {
                    setResult(Constants.PermissionAndRequests.HELP_RESULT);
                    finish();
                }
            }
        });
    }

    @Override
    public void addPolicy() {
        Intent intent = new Intent(this, AddPolicyActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        startActivityForResult(intent,Constants.PermissionAndRequests.ADD_POLICY_REQUEST);
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
    public void claimSupport() {
        Intent intent = new Intent(this, ClaimSupportActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        startActivityForResult(intent,Constants.PermissionAndRequests.CLAIMS_REQUEST);
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
    public void nomineeDashboard() {
        Intent intent = new Intent(this, NomineeSupportActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getuId());
        startActivityForResult(intent,Constants.PermissionAndRequests.NOMINEE_DASHBOARD_REQUEST);
        finish();
    }

    @Override
    public void getHelp() {

    }

    @Override
    public void logOut() {
        super.startProgress();
        cancelNotifications();
        viewModel.logOut().observe(HelpActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                HelpActivity.super.endProgress();
                if(aBoolean){
                    getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE).edit().clear().apply();
                    getSharedPreferences(Constants.Policy.UPDATED_SHARED_PREFRENCE,MODE_PRIVATE).edit().clear().apply();

                    Intent intent = new Intent(HelpActivity.this, LoginSignUpActivity.class);
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
                        Intent intent = new Intent(HelpActivity.this, PremiumDuesReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(HelpActivity.this, (int) notifications.get(i).getId(), intent, PendingIntent.FLAG_NO_CREATE);
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
