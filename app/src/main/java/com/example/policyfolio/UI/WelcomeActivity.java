package com.example.policyfolio.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.policyfolio.UI.Home.HomeActivity;
import com.example.policyfolio.UI.LoginSignUp.LoginSignUpActivity;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Data.Firebase.Classes.LogInData;
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
        viewModel.initiateRepo(this);

        viewModel.getLoginStatus(sharedPreferences).observe(this, new Observer<LogInData>() {
            @Override
            public void onChanged(@Nullable LogInData logInData) {
                if(logInData ==null){
                    intent = new Intent(WelcomeActivity.this, LoginSignUpActivity.class);
                }
                else{
                    intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.LoginInInfo.LOGGED_IN, logInData.isLogin());
                    bundle.putInt(Constants.LoginInInfo.TYPE, logInData.getType());
                    bundle.putString(Constants.LoginInInfo.FIREBASE_UID, logInData.getFirebaseToken());
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
        },2000);
    }
}
