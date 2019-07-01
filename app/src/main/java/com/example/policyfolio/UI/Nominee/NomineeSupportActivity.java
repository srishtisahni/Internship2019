package com.example.policyfolio.UI.Nominee;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.example.policyfolio.Data.Local.Classes.Notifications;
import com.example.policyfolio.UI.Promotions.PromotionsActivity;
import com.example.policyfolio.UI.AddPolicy.AddPolicyActivity;
import com.example.policyfolio.UI.Base.BaseNavigationActivity;
import com.example.policyfolio.UI.Claim.ClaimSupportActivity;
import com.example.policyfolio.UI.Document.DocumentActivity;
import com.example.policyfolio.UI.Help.HelpActivity;
import com.example.policyfolio.UI.LoginSignUp.LoginSignUpActivity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;

import com.example.policyfolio.R;
import com.example.policyfolio.UI.Base.ParentChildNavigationCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Util.Receivers.PremiumDuesReceiver;
import com.example.policyfolio.ViewModels.WithUser.NomineeViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class NomineeSupportActivity extends BaseNavigationActivity implements NomineeCallback, NavigationView.OnNavigationItemSelectedListener, ParentChildNavigationCallback {

    private NomineeViewModel viewModel;

    private NomineeDashboardFragment nomineeDashboardFragment;
    private AddNomineeFragment addNomineeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        super.setCallback(this);
        super.setMenuSelection(R.id.nominee_support);

        viewModel = ViewModelProviders.of(this).get(NomineeViewModel.class);
        viewModel.initiateRepo(this);

        addDashboard();
    }

    private void addDashboard() {
        getSupportActionBar().setTitle("Nominee Dashboard");
        super.endProgress();

        if(nomineeDashboardFragment == null){
            nomineeDashboardFragment = new NomineeDashboardFragment(this);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,nomineeDashboardFragment).commit();
    }

    @Override
    public void addNominee() {
        getSupportActionBar().setTitle("Add Nominee");
        if(addNomineeFragment == null){
            addNomineeFragment = new AddNomineeFragment(this);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder,addNomineeFragment).commit();
    }

    @Override
    public void done() {
        super.startProgress();
        viewModel.addNominee().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                NomineeSupportActivity.super.endProgress();
                if (aBoolean) {
                    getSupportFragmentManager().beginTransaction().remove(addNomineeFragment).commit();
                    addNomineeFragment = null;
                    getSupportActionBar().setTitle("Nominee Dashboard");
                } else {
                    showSnackbar("Unable to add Nominee.");
                }
            }
        });
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
    public void claimSupport() {
        Intent intent = new Intent(this, ClaimSupportActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.CLAIMS_REQUEST);
        finish();
    }

    @Override
    public void promotions() {
        Intent intent = new Intent(this, PromotionsActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.PROMOTIONS_REQUEST);
        finish();
    }

    @Override
    public void getHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivityForResult(intent,Constants.PermissionAndRequests.HELP_REQUEST);
        finish();
    }

    @Override
    public void logOut() {
        super.startProgress();
        cancelNotifications();
        viewModel.logOut().observe(NomineeSupportActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                NomineeSupportActivity.super.endProgress();
                if(aBoolean){
                    getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE).edit().clear().apply();
                    getSharedPreferences(Constants.Policy.UPDATED_SHARED_PREFRENCE,MODE_PRIVATE).edit().clear().apply();

                    Intent intent = new Intent(NomineeSupportActivity.this, LoginSignUpActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    showSnackbar("LogOut Failed");
                }
            }
        });
    }

    @Override
    public void nomineeDashboard() {

    }

    private void cancelNotifications() {
        getSharedPreferences(Constants.Notification.NOTIFICATION_SHARED_PREFERENCE,MODE_PRIVATE).edit().clear().apply();
        viewModel.getAllNotificatios().observe(this, new Observer<List<Notifications>>() {
            @Override
            public void onChanged(List<Notifications> notifications) {
                if(!notifications.isEmpty()) {
                    for (int i = 0; i < notifications.size(); i++) {
                        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                        Intent intent = new Intent(NomineeSupportActivity.this, PremiumDuesReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(NomineeSupportActivity.this, (int) notifications.get(i).getId(), intent, PendingIntent.FLAG_NO_CREATE);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(addNomineeFragment!=null){
                getSupportActionBar().setTitle("Nominee Dashboard");
                getSupportFragmentManager().beginTransaction().remove(addNomineeFragment).commit();
                addNomineeFragment = null;
            }
            else {
                super.onBackPressed();
            }
        }
    }
}
