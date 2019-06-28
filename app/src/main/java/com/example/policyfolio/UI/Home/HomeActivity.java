package com.example.policyfolio.UI.Home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.policyfolio.Data.Local.Classes.Notifications;
import com.example.policyfolio.UI.LoginSignUp.LoginSignUpActivity;
import com.example.policyfolio.UI.Nominee.NomineeSupportActivity;
import com.example.policyfolio.UI.Promotions.PromotionsActivity;
import com.example.policyfolio.UI.AddPolicy.AddPolicyActivity;
import com.example.policyfolio.UI.Base.BaseNavigationActivity;
import com.example.policyfolio.UI.Claim.ClaimSupportActivity;
import com.example.policyfolio.UI.Document.DocumentActivity;
import com.example.policyfolio.UI.Help.HelpActivity;
import com.example.policyfolio.UI.PopUps.PopUpActivity;
import com.example.policyfolio.UI.Base.ParentChildNavigationCallback;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Data.Local.Classes.Policy;
import com.example.policyfolio.Data.Local.Classes.User;
import com.example.policyfolio.R;
import com.example.policyfolio.Util.Receivers.PremiumDuesReceiver;
import com.example.policyfolio.ViewModels.HomeViewModel;


import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;


public class HomeActivity extends BaseNavigationActivity implements HomeCallback, ParentChildNavigationCallback {

    private HomeViewModel viewModel;

    private HomeStartupFragment homeStartupFragment;
    private HomePoliciesFragment homePoliciesFragment;

    private Toast timeToast;
    private SharedPreferences notifications;
    private long updatedEpoch;
    private Intent popUpIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        super.setCallback(this);
        getSupportActionBar().setTitle("Policy Folio");

        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        viewModel.initiateRepo(this);

        notifications = getSharedPreferences(Constants.Notification.NOTIFICATION_SHARED_PREFERENCE,MODE_PRIVATE);

        getUpdated();
        fetchInfo();
        getUser();
    }

    private void getUpdated() {
        SharedPreferences updated = getSharedPreferences(Constants.Policy.UPDATED_SHARED_PREFRENCE,MODE_PRIVATE);
        updatedEpoch = updated.getLong(Constants.Policy.UPDATED_SHARED_PREFRENCE,0);
    }

    private void getUser() {
        viewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if(user!=null){
                    getTimeToast(user.getLastUpdated());
                    viewModel.setType(user.getType());
                    if(user.getName()!=null){
                        getSupportActionBar().setTitle(user.getFirstName() + "'s Profile");
                        HomeActivity.super.setNameText(user.getName());
                        HomeActivity.super.updateName();
                    }
                    if(!user.isComplete()){
                        viewModel.addDocumentsVault().observe(HomeActivity.this, new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean aBoolean) {
                                if(aBoolean)
                                    Log.e("DOCUMENT VAULT","Added");
                                else
                                    Log.e("DOCUMENT VAULT","Error!");
                            }
                        });
                        if(popUpIntent == null) {
                            popUpIntent = new Intent(HomeActivity.this, PopUpActivity.class);
                            popUpIntent.putExtra(Constants.PopUps.POPUP_TYPE, Constants.PopUps.Type.INFO_POPUP);

                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.User.ID, user.getId());
                            bundle.putString(Constants.User.NAME, user.getName());
                            bundle.putLong(Constants.User.BIRTHDAY, user.getBirthday());
                            bundle.putInt(Constants.User.GENDER, user.getGender());
                            bundle.putString(Constants.User.CITY, user.getCity());
                            bundle.putString(Constants.User.EMAIL, user.getEmail());
                            bundle.putString(Constants.User.PHONE, user.getPhone());
                            bundle.putInt(Constants.User.LOGIN_TYPE, user.getType());
                            popUpIntent.putExtras(bundle);

                            startActivityForResult(popUpIntent, Constants.PermissionAndRequests.UPDATE_REQUEST);
                        }
                    }
                    else{
                        policyUpdate();                                       //Render UI based on the user info
                    }
                }
            }
        });
    }

    private void getTimeToast(Long lastUpdated) {
        Long timeDifference = (System.currentTimeMillis()/1000) - lastUpdated;
        long days = timeDifference/(60*60*24);
        timeDifference = timeDifference%(60*60*24);
        long hours = timeDifference/(60*60);
        timeDifference = timeDifference%(60*60);
        long minutes = timeDifference/60;
        timeDifference = timeDifference%60;
        long sec = timeDifference;
        String time = "";
        if(days!=0)
            time = time + days + " days ";
        else if(hours!=0)
            time = time + hours + " hours ";
        else if(minutes!=0)
            time = time + minutes + " mins ";
        else if(sec!=0)
            time = time + sec + " secs ";
        if(time.length()>0) {
            time = time + "ago";
            if(timeToast == null) {
                timeToast = Toast.makeText(this, "Updated " + time, Toast.LENGTH_SHORT);
                timeToast.show();
            }
            else {
                timeToast.setText("Updated " + time);
            }
        }
    }

    private void fetchInfo() {
        Bundle bundle = getIntent().getExtras();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE);
        if(bundle.getBoolean(Constants.LoginInInfo.LOGGED_IN)){                     //Update the shared preference with the latest information
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.LoginInInfo.LOGGED_IN,true);
            editor.putInt(Constants.LoginInInfo.TYPE,bundle.getInt(Constants.LoginInInfo.TYPE));
            editor.putString(Constants.LoginInInfo.FIREBASE_UID,bundle.getString(Constants.LoginInInfo.FIREBASE_UID));
            editor.apply();
            viewModel.setType(bundle.getInt(Constants.LoginInInfo.TYPE));
            viewModel.setUid(bundle.getString(Constants.LoginInInfo.FIREBASE_UID));
        }
    }

    private void policyUpdate() {
        viewModel.getPolicies().observe(this, new Observer<List<Policy>>() {
            @Override
            public void onChanged(List<Policy> policies) {
                if(policies!=null){                                                                 //Rendering multiple policy fragment
                    if(policies.size()==0)                                                          //If no policies are added yet, render zero policy fragment
                        zeroPoliciesFragment();
                    else {
                        Long time = Long.valueOf(0);
                        for(int i=0;i<policies.size();i++){
                            if(time < policies.get(i).getLastUpdated())
                                time = policies.get(i).getLastUpdated();
                        }
                        getTimeToast(time);
                        policiesFragment(policies);
                        if(System.currentTimeMillis()/1000 - updatedEpoch > Constants.Time.EPOCH_DAY)
                            updatePaidStatus(policies);
                        notificationChecker(policies);
                    }
                }
            }
        });
    }

    private void notificationChecker(final List<Policy> policies) {
        for(int i=0;i<policies.size();i++){
            final Policy policy = policies.get(i);
            Boolean added = notifications.getBoolean(policy.getPolicyNumber(),false);
            if(!added && policy.getUserId().equals(viewModel.getUid()))
                addNotification(policy);
        }
    }

    private void addNotification(Policy policy) {
        notifications.edit().putBoolean(policy.getPolicyNumber(),true).apply();

        final int frequency = policy.getFrequency();
        final long premium = policy.getNextDueDate();
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Intent intent = new Intent(this, PremiumDuesReceiver.class);
        intent.putExtra(Constants.Notification.POLICY_NUMBER,policy.getPolicyNumber());

        Notifications notifications = new Notifications();
        notifications.setPolicyNumber(policy.getPolicyNumber());
        viewModel.addNotifications(notifications,frequency).observe(this, new Observer<List<Long>>() {
            @Override
            public void onChanged(List<Long> longs) {
                Log.e("SIZE",longs.size()+"");
                if(longs.size()!=0) {
                    long time;

                    long dayId = longs.get(0);
                    long weekId = longs.get(1);
                    long twoWeeksId, oneMonthId, twoMonthsId;

                    intent.putExtra(Constants.Notification.ID, dayId);
                    intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.DAY);
                    time = (premium - Constants.Time.EPOCH_DAY) * 1000;
                    if (time > System.currentTimeMillis())
                        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) dayId, intent, 0));
                    else
                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), PendingIntent.getBroadcast(HomeActivity.this, (int) dayId, intent, 0));

                    intent.putExtra(Constants.Notification.ID, weekId);
                    intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.WEEK);
                    time = (premium - Constants.Time.EPOCH_WEEK) * 1000;

                    if (time > System.currentTimeMillis())
                        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) weekId, intent, 0));

                    switch (frequency) {
                        case Constants.Policy.Premium.PREMIUM_ANNUALLY:
                            oneMonthId = longs.get(2);
                            intent.putExtra(Constants.Notification.ID, oneMonthId);
                            intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.MONTH);
                            time = (premium - Constants.Time.EPOCH_MONTH) * 1000;
                            if (time > System.currentTimeMillis())
                                alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) oneMonthId, intent, 0));

                            twoMonthsId = longs.get(3);
                            intent.putExtra(Constants.Notification.ID, twoMonthsId);
                            intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.TWO_MONTHS);
                            time = (premium - Constants.Time.EPOCH_MONTH * 2) * 1000;
                            if (time > System.currentTimeMillis())
                                alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) twoMonthsId, intent, 0));
                            break;
                        case Constants.Policy.Premium.PREMIUM_BI_ANNUALLY:
                            oneMonthId = longs.get(2);
                            intent.putExtra(Constants.Notification.ID, oneMonthId);
                            intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.MONTH);
                            time = (premium - Constants.Time.EPOCH_MONTH) * 1000;
                            if (time > System.currentTimeMillis())
                                alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) oneMonthId, intent, 0));
                            break;
                        case Constants.Policy.Premium.PREMIUM_MONTHLY:
                            twoWeeksId = longs.get(2);
                            intent.putExtra(Constants.Notification.ID, twoWeeksId);
                            intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.TWO_WEEKS);
                            time = (premium - Constants.Time.EPOCH_WEEK * 2) * 1000;
                            if (time > System.currentTimeMillis())
                                alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) twoWeeksId, intent, 0));
                            break;
                        case Constants.Policy.Premium.PREMIUM_QUATERLY:
                            oneMonthId = longs.get(2);
                            intent.putExtra(Constants.Notification.ID, oneMonthId);
                            intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.MONTH);
                            time = (premium - Constants.Time.EPOCH_MONTH) * 1000;
                            if (time > System.currentTimeMillis())
                                alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) oneMonthId, intent, 0));
                            break;
                    }
                }
            }
        });
    }

    private void policiesFragment(List<Policy> policies) {
        if(homePoliciesFragment==null) {
            homePoliciesFragment = new HomePoliciesFragment(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, homePoliciesFragment).commit();
        }
        homePoliciesFragment.setPolicies(policies);

        super.endProgress();
    }

    private void updatePaidStatus(List<Policy> policies) {
        updatedEpoch = System.currentTimeMillis()/1000;
        for(int i =0;i<policies.size();i++) {
            if (policies.get(i).getNextDueDate() < System.currentTimeMillis() / 1000)
                policies.get(i).setPaid(false);
            else
                policies.get(i).setPaid(true);
        }
        viewModel.updatePolicies(policies).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    SharedPreferences updated = getSharedPreferences(Constants.Policy.UPDATED_SHARED_PREFRENCE,MODE_PRIVATE);
                    SharedPreferences.Editor edit = updated.edit();
                    edit.putLong(Constants.Policy.UPDATED_SHARED_PREFRENCE,System.currentTimeMillis()/1000);
                    edit.apply();
                }
            }
        });
    }

    private void zeroPoliciesFragment() {
        homeStartupFragment = new HomeStartupFragment(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, homeStartupFragment).commit();

        super.endProgress();
    }

    @Override
    public void addPolicy(int type) {
        Intent intent = new Intent(this, AddPolicyActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        intent.putExtra(Constants.InsuranceProviders.TYPE,type);
        intent.putExtra(Constants.User.LOGIN_TYPE,viewModel.getType());
        startActivityForResult(intent,Constants.PermissionAndRequests.ADD_POLICY_REQUEST);
    }

    @Override
    public void addPolicy() {
        Intent intent = new Intent(this, AddPolicyActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        intent.putExtra(Constants.User.LOGIN_TYPE,viewModel.getType());
        startActivityForResult(intent,Constants.PermissionAndRequests.ADD_POLICY_REQUEST);
    }

    @Override
    public void claimSupport() {
        Intent intent = new Intent(this, ClaimSupportActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        intent.putExtra(Constants.User.LOGIN_TYPE,viewModel.getType());
        startActivityForResult(intent,Constants.PermissionAndRequests.CLAIMS_REQUEST);
    }

    @Override
    public void documentVault() {
        Intent intent = new Intent(this, DocumentActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        intent.putExtra(Constants.User.LOGIN_TYPE,viewModel.getType());
        startActivityForResult(intent,Constants.PermissionAndRequests.DOCUMENTS_REQUEST);
    }

    @Override
    public void promotions() {
        Intent intent = new Intent(this, PromotionsActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        intent.putExtra(Constants.User.LOGIN_TYPE,viewModel.getType());
        startActivityForResult(intent,Constants.PermissionAndRequests.PROMOTIONS_REQUEST);
    }

    @Override
    public void getHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        intent.putExtra(Constants.User.LOGIN_TYPE,viewModel.getType());
        startActivityForResult(intent,Constants.PermissionAndRequests.HELP_REQUEST);
    }

    @Override
    public void nomineeDashboard() {
        Intent intent = new Intent(this, NomineeSupportActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        intent.putExtra(Constants.User.LOGIN_TYPE,viewModel.getType());
        startActivityForResult(intent,Constants.PermissionAndRequests.NOMINEE_DASHBOARD_REQUEST);
    }

    @Override
    public void logOut() {
        super.startProgress();
        cancelNotifications();
        viewModel.logOut().observe(HomeActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                HomeActivity.super.endProgress();
                if(aBoolean){
                    getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE).edit().clear().apply();
                    getSharedPreferences(Constants.Policy.UPDATED_SHARED_PREFRENCE,MODE_PRIVATE).edit().clear().apply();

                    Intent intent = new Intent(HomeActivity.this, LoginSignUpActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    HomeActivity.super.showSnackbar("LogOut Failed");
                }
            }
        });
    }

    private void cancelNotifications() {
        notifications.edit().clear().apply();
        viewModel.getAllNotificatios().observe(this, new Observer<List<Notifications>>() {
            @Override
            public void onChanged(List<Notifications> notifications) {
                if(!notifications.isEmpty()) {
                    for (int i = 0; i < notifications.size(); i++) {
                        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                        Intent intent = new Intent(HomeActivity.this, PremiumDuesReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, (int) notifications.get(i).getId(), intent, PendingIntent.FLAG_NO_CREATE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Constants.PermissionAndRequests.UPDATE_REQUEST:
                if(resultCode == Constants.PermissionAndRequests.UPDATE_RESULT) {
                    popUpIntent = null;                                  //Render Fragments based on user information
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.removeAllSelections();
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
