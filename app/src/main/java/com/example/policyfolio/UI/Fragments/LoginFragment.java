package com.example.policyfolio.UI.Fragments;


import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.policyfolio.Constants;
import com.example.policyfolio.R;
import com.example.policyfolio.UI.CallBackListeners.LoginFragmentCallback;
import com.example.policyfolio.ViewModels.LoginSignUpViewModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private View rootView;
    private Context context;
    private LoginSignUpViewModel viewModel;
    private LoginFragmentCallback callback;

    private EditText emailPhone;
    private EditText password;
    private Button login;

    private CircleImageView google;
    private CircleImageView facebook;
    private CircleImageView phone;

    private LoginButton facebookLogin;
    private CallbackManager facebookCallbackManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public LoginFragment(Context context, LoginFragmentCallback callback) {
        this.context = context;
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
        facebookCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                        if(isLoggedIn){
                            viewModel.fetchFacebookData(accessToken).observe(LoginFragment.this, new Observer<com.example.policyfolio.Data.Facebook>() {
                                @Override
                                public void onChanged(@Nullable com.example.policyfolio.Data.Facebook facebook) {
                                    if(facebook!=null){
                                        callback.SignUp(facebook);
                                    }
                                    else {
                                        Toast.makeText(context,"Login Error Occurred", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(context,"Access Denied",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(context,"Login Error Occurred", Toast.LENGTH_LONG).show();
                        exception.printStackTrace();
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

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
