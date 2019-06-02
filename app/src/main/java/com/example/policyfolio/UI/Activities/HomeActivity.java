package com.example.policyfolio.UI.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.policyfolio.Constants;
import com.example.policyfolio.R;
import com.example.policyfolio.ViewModels.HomeViewModel;

public class HomeActivity extends AppCompatActivity {

    private HomeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);

        Bundle bundle = getIntent().getExtras();
        if(bundle.getBoolean(Constants.Login.LOGGED_IN,false)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.Login.LOGGED_IN,true);
            editor.putInt(Constants.Login.TYPE,bundle.getInt(Constants.Login.TYPE,-1));
            editor.putString(Constants.Login.FIREBASE_TOKEN,bundle.getString(Constants.Login.FIREBASE_TOKEN,null));
            editor.commit();

            viewModel.setType(bundle.getInt(Constants.Login.TYPE,-1));
            viewModel.setFirebaseToken(bundle.getString(Constants.Login.FIREBASE_TOKEN,null));
        }
    }

}
