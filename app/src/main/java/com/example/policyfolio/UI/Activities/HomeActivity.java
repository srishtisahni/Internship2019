package com.example.policyfolio.UI.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.policyfolio.Repo.Database.DataClasses.Notifications;
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

    private long updatedEpoch;

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
                fragmentHolder.setAlpha(1f);
                progressBar.setVisibility(View.GONE);
                if(user!=null){
                    getTimeToast(user.getLastUpdated());
                    if(user.getName()!=null){
                        getSupportActionBar().setTitle(user.getFirstName() + "'s Profile");
                        name.setText(user.getFirstName());
                    }
                    if(!user.isComplete()){                                        //If user information is not complete, generate a pop up to fetch information
                        Intent intent = new Intent(HomeActivity.this,PopUpActivity.class);
                        intent.putExtra(Constants.PopUps.POPUP_TYPE,Constants.PopUps.Type.INFO_POPUP);
                        intent.putExtra(Constants.LoginInInfo.FIREBASE_UID,viewModel.getUid());
                        startActivityForResult(intent, Constants.PermissionAndRequests.UPDATE_REQUEST);
                    }
                    else{
                        policyUpdate();                                       //Render UI based on the user info
                    }
                }
            }
        });
    }

    private void getTimeToast(Long lastUpdated) {
        if(timeToast!=null)
            timeToast.cancel();
        Long timeDiffernce = (System.currentTimeMillis()/1000) - lastUpdated;
        long days = timeDiffernce/(60*60*24);
        timeDiffernce = timeDiffernce%(60*60*24);
        long hours = timeDiffernce/(60*60);
        timeDiffernce = timeDiffernce%(60*60);
        long minutes = timeDiffernce/60;
        timeDiffernce = timeDiffernce%60;
        long sec = timeDiffernce;
        String time = "";
        if(days!=0)
            time = time + days + " days ";
        if(hours!=0)
            time = time + hours + " hours";
        if(minutes!=0)
            time = time + minutes + " mins ";
        if(sec!=0)
            time = time + sec + " secs ";
        if(time.length()>0) {
            time = time + "ago";
            timeToast = Toast.makeText(this,"Updated "+time,Toast.LENGTH_SHORT);
        }
        else {
            timeToast = null;
        }
        if(timeToast!=null)
            timeToast.show();
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
        fragmentHolder.setAlpha(.4f);
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getPolicies().observe(this, new Observer<List<Policy>>() {
            @Override
            public void onChanged(List<Policy> policies) {
                fragmentHolder.setAlpha(1f);
                progressBar.setVisibility(View.GONE);
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

    private void notificationChecker(List<Policy> policies) {
        for(int i=0;i<policies.size();i++){
            final Policy policy = policies.get(i);
            viewModel.fetchNotifications(policy.getPolicyNumber()).observe(this, new Observer<List<Notifications>>() {
                @Override
                public void onChanged(List<Notifications> notifications) {
                    if(notifications.size() == 0){
                        addNotification(policy);
                    }
                }
            });
        }
    }

    private void addNotification(Policy policy) {
        final int frequency = policy.getFrequency();
        final long premium = policy.getNextDueDate();
        final AlarmManager twoMonths = (AlarmManager) getSystemService(ALARM_SERVICE);
        final AlarmManager oneMonth = (AlarmManager) getSystemService(ALARM_SERVICE);
        final AlarmManager twoWeeks = (AlarmManager) getSystemService(ALARM_SERVICE);
        final AlarmManager oneWeek = (AlarmManager) getSystemService(ALARM_SERVICE);
        final AlarmManager oneDay = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Intent intent = new Intent(this, PremiumDuesReceiver.class);
        intent.putExtra(Constants.Notification.POLICY_NUMBER,policy.getPolicyNumber());

        Notifications notifications = new Notifications();
        notifications.setPolicyNumber(policy.getPolicyNumber());
        viewModel.addNotifications(notifications,frequency).observe(this, new Observer<List<Long>>() {
            @Override
            public void onChanged(List<Long> longs) {
                if(longs.size()!=0) {
                    long time;

                    long dayId = longs.get(0);
                    long weekId = longs.get(1);
                    long twoWeeksId, oneMonthId, twoMonthsId;

                    intent.putExtra(Constants.Notification.ID, dayId);
                    intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.DAY);
                    time = (premium - Constants.Time.EPOCH_DAY) * 1000;
                    if (time > System.currentTimeMillis())
                        oneDay.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) dayId, intent, 0));

                    intent.putExtra(Constants.Notification.ID, weekId);
                    intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.WEEK);
                    time = (premium - Constants.Time.EPOCH_WEEK) * 1000;
                    if (time > System.currentTimeMillis())
                        oneWeek.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) weekId, intent, 0));

                    switch (frequency) {
                        case Constants.Policy.Premium.PREMIUM_ANNUALLY:
                            oneMonthId = longs.get(2);
                            intent.putExtra(Constants.Notification.ID, oneMonthId);
                            intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.MONTH);
                            time = (premium - Constants.Time.EPOCH_MONTH) * 1000;
                            if (time > System.currentTimeMillis())
                                oneMonth.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) oneMonthId, intent, 0));

                            twoMonthsId = longs.get(3);
                            intent.putExtra(Constants.Notification.ID, twoMonthsId);
                            intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.TWO_MONTHS);
                            time = (premium - Constants.Time.EPOCH_MONTH * 2) * 1000;
                            if (time > System.currentTimeMillis())
                                twoMonths.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) twoMonthsId, intent, 0));
                            break;
                        case Constants.Policy.Premium.PREMIUM_BI_ANNUALLY:
                            oneMonthId = longs.get(2);
                            intent.putExtra(Constants.Notification.ID, oneMonthId);
                            intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.MONTH);
                            time = (premium - Constants.Time.EPOCH_MONTH) * 1000;
                            if (time > System.currentTimeMillis())
                                oneMonth.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) oneMonthId, intent, 0));
                            break;
                        case Constants.Policy.Premium.PREMIUM_MONTHLY:
                            twoWeeksId = longs.get(2);
                            intent.putExtra(Constants.Notification.ID, twoWeeksId);
                            intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.TWO_WEEKS);
                            time = (premium - Constants.Time.EPOCH_WEEK * 2) * 1000;
                            if (time > System.currentTimeMillis())
                                twoWeeks.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) twoWeeksId, intent, 0));
                            break;
                        case Constants.Policy.Premium.PREMIUM_QUATERLY:
                            oneMonthId = longs.get(2);
                            intent.putExtra(Constants.Notification.ID, oneMonthId);
                            intent.putExtra(Constants.Notification.TYPE, Constants.Notification.Type.MONTH);
                            time = (premium - Constants.Time.EPOCH_MONTH) * 1000;
                            if (time > System.currentTimeMillis())
                                oneMonth.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(HomeActivity.this, (int) oneMonthId, intent, 0));
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

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        fragmentHolder.setAlpha(.4f);
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getAllNotificatios().observe(this, new Observer<List<Notifications>>() {
            @Override
            public void onChanged(List<Notifications> notifications) {
                for(int i=0;i<notifications.size();i++) {
                    AlarmManager alarm= (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent intent=new Intent(HomeActivity.this,PremiumDuesReceiver.class);
                    PendingIntent pendingIntent=PendingIntent.getBroadcast(HomeActivity.this, (int) notifications.get(i).getId(),intent,PendingIntent.FLAG_NO_CREATE);
                    if(pendingIntent!=null) {
                        alarm.cancel(pendingIntent);
                    }
                    viewModel.deleteAllNotifications();
                    viewModel.logOut().observe(HomeActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            fragmentHolder.setAlpha(1f);
                            progressBar.setVisibility(View.GONE);
                            if(aBoolean){
                                SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.commit();

                                Intent intent = new Intent(HomeActivity.this,LoginSignUpActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(HomeActivity.this,"LogOut Failed",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.PermissionAndRequests.UPDATE_REQUEST && resultCode == Constants.PermissionAndRequests.UPDATE_RESULT){
//            policyUpdate();                                       //Render Fragments based on user information
        }
        if(requestCode == Constants.PermissionAndRequests.ADD_POLICY_REQUEST && resultCode == Constants.PermissionAndRequests.ADD_POLICY_RESULT){
//            policies();
        }
    }

}
