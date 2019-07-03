package com.example.policyfolio.UI.LoginSignUp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.policyfolio.Data.Firebase.Classes.LogInData;
import com.example.policyfolio.R;
import com.example.policyfolio.UI.Home.HomeActivity;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.ViewModels.LoginSignUpViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {

    private View rootView;
    private LoginSignUpViewModel viewModel;

    private SharedPreferences sharedPrefrences;
    private WelcomeCallback callback;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public WelcomeFragment(WelcomeCallback callback, SharedPreferences sharedPreferences){
        this.callback = callback;
        this.sharedPrefrences = sharedPreferences;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(LoginSignUpViewModel.class);
        viewModel.initiateRepo(getContext());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewModel.getLoginStatus(sharedPrefrences).observe(WelcomeFragment.this, new Observer<LogInData>() {
                    @Override
                    public void onChanged(@Nullable LogInData logInData) {
                        if(logInData ==null){
                            callback.openLoginSignUp();
                        }
                        else{
                            callback.openHome(logInData);
                        }
                    }
                });
            }
        },2000);

        return rootView;
    }

}
