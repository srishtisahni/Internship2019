package com.example.policyfolio.UI.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.policyfolio.Repo.Database.DataClasses.Notifications;
import com.example.policyfolio.UI.Activities.NavigationActionActivities.ClaimSupportActivity;
import com.example.policyfolio.UI.Activities.NavigationActionActivities.HelpActivity;
import com.example.policyfolio.UI.Activities.NavigationActionActivities.NomineeSupportActivity;
import com.example.policyfolio.UI.Activities.NavigationActionActivities.PromotionsActivity;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Repo.Database.DataClasses.Policy;
import com.example.policyfolio.Repo.Database.DataClasses.User;
import com.example.policyfolio.R;
import com.example.policyfolio.UI.Activities.NavigationActionActivities.AddPolicyActivity;
import com.example.policyfolio.Util.CallBackListeners.HomeCallback;
import com.example.policyfolio.UI.Fragments.HomePoliciesFragment;
import com.example.policyfolio.UI.Fragments.HomeStartupFragment;
import com.example.policyfolio.Util.Receivers.PremiumDuesReceiver;
import com.example.policyfolio.ViewModels.HomeViewModel;
import com.google.android.material.navigation.NavigationView;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeCallback {

    private HomeViewModel viewModel;

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private FrameLayout snackBar;
    private NavigationView navigationView;

    private TextView name;

    private HomeStartupFragment homeStartupFragment;
    private HomePoliciesFragment homePoliciesFragment;

    private Toast timeToast;

    private SharedPreferences notifications;

    private long updatedEpoch;

    private Intent popUpIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);
        snackBar = findViewById(R.id.snackbar_action);
        drawer = findViewById(R.id.drawer_layout);
        name = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.nav_name);
        navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PolicyFolio");

        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        viewModel.initiateRepo(this);

        notifications = getSharedPreferences(Constants.Notification.NOTIFICATION_SHARED_PREFERENCE,MODE_PRIVATE);

        getUpdated();
        setUpDrawer();
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
                    if(user.getName()!=null){
                        getSupportActionBar().setTitle(user.getFirstName() + "'s Profile");
                        name.setText(user.getName());
                    }
                    if(!user.isComplete()){
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

        fragmentHolder.setAlpha(1f);
        progressBar.setVisibility(View.GONE);
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

        fragmentHolder.setAlpha(1f);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void addPolicy() {
        Intent intent = new Intent(this, AddPolicyActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        startActivityForResult(intent,Constants.PermissionAndRequests.ADD_POLICY_REQUEST);
    }

    @Override
    public void addPolicy(int type) {
        Intent intent = new Intent(this, AddPolicyActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        intent.putExtra(Constants.InsuranceProviders.TYPE,type);
        startActivityForResult(intent,Constants.PermissionAndRequests.ADD_POLICY_REQUEST);
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
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.add_policy:
                item.setChecked(false);
                addPolicy();
                break;
            case R.id.logout:
                item.setChecked(false);
                logOut();
                break;
            case R.id.nominee_support:
                item.setChecked(false);
                nomineeDashboard();
                break;
            case R.id.help:
                item.setChecked(false);
                getHelp();
                break;
            case R.id.promotions:
                item.setChecked(false);
                promotions();
                break;
            case R.id.claim_support:
                item.setChecked(false);
                claimSupport();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void claimSupport() {
        Intent intent = new Intent(this, ClaimSupportActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        startActivityForResult(intent,Constants.PermissionAndRequests.CLAIMS_REQUEST);
    }

    private void promotions() {
        Intent intent = new Intent(this, PromotionsActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        startActivityForResult(intent,Constants.PermissionAndRequests.PROMOTIONS_REQUEST);
    }

    private void getHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        startActivityForResult(intent,Constants.PermissionAndRequests.HELP_REQUEST);
    }

    private void nomineeDashboard() {
        Intent intent = new Intent(this, NomineeSupportActivity.class);
        intent.putExtra(Constants.User.ID,viewModel.getUid());
        startActivityForResult(intent,Constants.PermissionAndRequests.NOMINEE_DASHBOARD_REQUEST);
    }

    private void logOut() {
        fragmentHolder.setAlpha(.4f);
        progressBar.setVisibility(View.VISIBLE);
        cancelNotifications();
        viewModel.logOut().observe(HomeActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                fragmentHolder.setAlpha(1f);
                progressBar.setVisibility(View.GONE);
                if(aBoolean){
                    getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE).edit().clear().apply();
                    getSharedPreferences(Constants.Policy.UPDATED_SHARED_PREFRENCE,MODE_PRIVATE).edit().clear().apply();

                    Intent intent = new Intent(HomeActivity.this,LoginSignUpActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(HomeActivity.this,"LogOut Failed",Toast.LENGTH_LONG).show();
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
                if(resultCode == Constants.PermissionAndRequests.UPDATE_RESULT)
                    popUpIntent = null;                                  //Render Fragments based on user information
                break;
            case Constants.PermissionAndRequests.ADD_POLICY_REQUEST:
                break;
            case Constants.PermissionAndRequests.HELP_REQUEST:
                if(resultCode == Constants.PermissionAndRequests.HELP_RESULT)
                    Toast.makeText(this,"We will get back on your query as soon as possible",Toast.LENGTH_LONG).show();
                break;
        }
    }

}
