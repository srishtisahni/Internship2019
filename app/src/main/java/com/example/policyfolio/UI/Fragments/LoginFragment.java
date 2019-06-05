package com.example.policyfolio.UI.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.Facebook;
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

    private EditText emailText;
    private EditText password;
    private TextView error;
    private TextView forgetPassword;
    private Button login;

    private CircleImageView google;
    private CircleImageView facebook;
    private CircleImageView phone;
    private CircleImageView email;
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
        viewModel.initiateRepo(getContext());

        emailText = rootView.findViewById(R.id.email);
        password = rootView.findViewById(R.id.password);
        login = rootView.findViewById(R.id.login);
        google = rootView.findViewById(R.id.google_signUp);
        facebook = rootView.findViewById(R.id.facebook_signUp);
        phone = rootView.findViewById(R.id.phone_signUp);
        email = rootView.findViewById(R.id.email_signUp);
        error = rootView.findViewById(R.id.error);
        forgetPassword = rootView.findViewById(R.id.forgot_password);

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.forgotPassword();
            }
        });

        if(viewModel.getEmail() != null){
            emailText.setText(viewModel.getEmail());
        }

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
                        case Constants.Facebook.Login.LOGGED_IN: viewModel.fetchFacebookData().observe(LoginFragment.this, new Observer<Facebook>() {
                            @Override
                            public void onChanged(@Nullable Facebook facebook) {
                                if(facebook!=null) {
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
                String email = emailText.getText().toString();
                String password = LoginFragment.this.password.getText().toString();
                error.setVisibility(View.GONE);
                if(email.equals("")){
                    error.setVisibility(View.VISIBLE);
                    error.setText("Email cannot be empty");
                }
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    error.setVisibility(View.VISIBLE);
                    error.setText("Invalid EmailPopUp");
                }
                if(password.length()<8){
                    error.setVisibility(View.VISIBLE);
                    error.setText("Passwoed should be atleast 8 characters long");
                }
                if(error.getVisibility()==View.GONE){
                    viewModel.setEmail(email);
                    viewModel.setPassword(password);
                    callback.Login();
                }
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
                callback.enterPhone();
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
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
