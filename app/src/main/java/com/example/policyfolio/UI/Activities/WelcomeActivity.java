package com.example.policyfolio.UI.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.LoggedIn;
import com.example.policyfolio.R;
import com.example.policyfolio.ViewModels.WelcomeViewModel;

public class WelcomeActivity extends AppCompatActivity {

    private WelcomeViewModel viewModel;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);

        viewModel = ViewModelProviders.of(this).get(WelcomeViewModel.class);
        viewModel.getLoginStatus(sharedPreferences).observe(this, new Observer<com.example.policyfolio.DataClasses.LoggedIn>() {
            @Override
            public void onChanged(@Nullable LoggedIn loggedIn) {
                if(loggedIn==null){
                    intent = new Intent(WelcomeActivity.this,LoginSignUpActivity.class);
                }
                else{
                    intent = new Intent(WelcomeActivity.this,HomeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.SharedPreferenceKeys.LOGGED_IN,loggedIn.isLogin());
                    bundle.putInt(Constants.SharedPreferenceKeys.TYPE,loggedIn.getType());
                    bundle.putString(Constants.SharedPreferenceKeys.FIREBASE_UID,loggedIn.getFirebseToken());
                    intent.putExtras(bundle);
                }
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        },3000);
    }
}
