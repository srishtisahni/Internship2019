package com.example.policyfolio.UI.Fragments;


import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.policyfolio.Constants;
import com.example.policyfolio.Data.Facebook;
import com.example.policyfolio.R;
import com.example.policyfolio.UI.CallBackListeners.LoginFragmentCallback;
import com.example.policyfolio.ViewModels.LoginSignUpViewModel;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private View rootView;
    private LoginSignUpViewModel viewModel;
    private LoginFragmentCallback callback;

    private EditText emailPhone;
    private EditText password;
    private Button login;

    private CircleImageView google;
    private CircleImageView facebook;
    private CircleImageView phone;
    private LoginButton facebookLogin;


    public LoginFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public LoginFragment(LoginFragmentCallback callback) {
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(LoginSignUpViewModel.class);

        emailPhone = rootView.findViewById(R.id.email_phone);
        password = rootView.findViewById(R.id.password);
        login = rootView.findViewById(R.id.login);
        google = rootView.findViewById(R.id.google_signUp);
        facebook = rootView.findViewById(R.id.facebook_signUp);
        phone = rootView.findViewById(R.id.phone_signUp);

        facebookLogin = rootView.findViewById(R.id.facebook_login);
        facebookLogin.setReadPermissions(Arrays.asList(Constants.Facebook.BIRTHDAY,
                Constants.Facebook.EMAIL,
                Constants.Facebook.GENDER,
                Constants.Facebook.PROFILE,
                Constants.Facebook.LOCATION));
        facebookLogin.setFragment(this);

        facebookCallback();
        setListeners();

        return rootView;
    }

    private void facebookCallback() {
        viewModel.facebookLogin().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer!=null){
                    switch (integer){
                        case Constants.Facebook.Login.LOGGED_IN: viewModel.getFacebookFetch().observe(LoginFragment.this, new Observer<Facebook>() {
                            @Override
                            public void onChanged(@Nullable Facebook facebook) {
                                if(facebook!=null) {
                                    viewModel.setType(Constants.Login.Type.FACEBOOK);
                                    viewModel.UpdateRepoFacebook(facebook);
                                    callback.FacebookSignUp(facebook);
                                }
                                else
                                    Toast.makeText(getContext(),"Login Error Occurred",Toast.LENGTH_LONG).show();
                            }
                        });
                            break;

                        case Constants.Facebook.Login.LOGIN_CANCELLED: Toast.makeText(getContext(),"Login Cancelled",Toast.LENGTH_LONG).show();
                            break;

                        case Constants.Facebook.Login.LOGIN_FAILED: Toast.makeText(getContext(),"Login Failed",Toast.LENGTH_LONG).show();
                            break;

                        case Constants.Facebook.Login.LOGIN_ERROR: Toast.makeText(getContext(),"Login Error Occurred",Toast.LENGTH_LONG).show();
                            break;
                    }
                }

                else
                    Toast.makeText(getContext(),"Login Error Occurred",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setListeners() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = getString(R.string.default_web_client_id);
                viewModel.initiateGoogleLogin(id, getContext());
                callback.GoogleSignUp();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLogin.performClick();
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.enterEmail();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onActivityResult(requestCode,resultCode,data);
    }
}
