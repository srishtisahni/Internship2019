package com.example.policyfolio.ui.nominee;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.example.policyfolio.data.local.classes.Notifications;
import com.example.policyfolio.ui.bottomsheets.ListBottomSheet;
import com.example.policyfolio.ui.promotions.PromotionsActivity;
import com.example.policyfolio.ui.addpolicy.AddPolicyActivity;
import com.example.policyfolio.ui.base.BaseNavigationActivity;
import com.example.policyfolio.ui.claim.ClaimSupportActivity;
import com.example.policyfolio.ui.document.DocumentActivity;
import com.example.policyfolio.ui.help.HelpActivity;
import com.example.policyfolio.ui.login.LoginSignUpActivity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import com.example.policyfolio.R;
import com.example.policyfolio.ui.base.ParentChildNavigationCallback;
import com.example.policyfolio.util.Constants;
import com.example.policyfolio.util.receivers.PremiumNotificationReceiver;
import com.example.policyfolio.viewmodels.NomineeViewModel;

import java.util.List;

public class NomineeSupportActivity extends BaseNavigationActivity implements NomineeCallback, ParentChildNavigationCallback {

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
    public void setBackgroundToGreen() {
        super.setFragmentHolderBg(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void setBackgroundToWhite() {
        super.setFragmentHolderBg(getResources().getColor(R.color.white));
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
                        Intent intent = new Intent(NomineeSupportActivity.this, PremiumNotificationReceiver.class);
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
        if(isSheetOpen()){
            closeListSheet();
        }
        else if (super.isDrawerOpen()) {
            super.closeDrawer();
        }
        else {
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


    @Override
    public void openListSheet(int type, RecyclerView.Adapter adapter) {
        ListBottomSheet listBottomSheet = new ListBottomSheet(type,adapter);
        super.expandSheet(listBottomSheet);
    }

    @Override
    public void closeListSheet() {
        super.collapseSheet();
    }
}
