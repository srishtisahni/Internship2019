package com.example.policyfolio.UI.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.policyfolio.Constants;
import com.example.policyfolio.Data.Facebook;
import com.example.policyfolio.R;
import com.example.policyfolio.UI.CallBackListeners.EmailPhoneCallback;
import com.example.policyfolio.UI.CallBackListeners.LoginFragmentCallback;
import com.example.policyfolio.UI.Fragments.EmailPhoneFragment;
import com.example.policyfolio.UI.Fragments.LoginFragment;
import com.example.policyfolio.UI.Fragments.SignUpFragment;
import com.example.policyfolio.ViewModels.LoginSignUpViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class LoginSignUpActivity extends AppCompatActivity implements LoginFragmentCallback, EmailPhoneCallback {

    private LoginFragment loginFragment;
    private SignUpFragment signUpFragment;
    private EmailPhoneFragment emailPhoneFragment;
    private LoginSignUpViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable dr = getResources().getDrawable(R.drawable.titlebar_icon);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(),
                Bitmap.createScaledBitmap(bitmap, 120, 120, true));
        getSupportActionBar().setIcon(d);

        viewModel = ViewModelProviders.of(this).get(LoginSignUpViewModel.class);

        loginFragment = new LoginFragment(this);
        signUpFragment = new SignUpFragment();
        emailPhoneFragment = new EmailPhoneFragment(this);

        addFragment(loginFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.Google.SIGN_IN_RC){
            viewModel.googleAuthentication(data, this).observe(this, new Observer<FirebaseUser>() {
                @Override
                public void onChanged(@Nullable FirebaseUser firebaseUser) {
                    if(firebaseUser!=null){
                        Intent intent = new Intent(LoginSignUpActivity.this,HomeActivity.class);
                        //Add Type
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(LoginSignUpActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        loginFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void FacebookSignUp(Facebook facebook) {
        viewModel.facebookFirebaseUser(this);
    }

    @Override
    public void enterEmail() {
        addFragment(emailPhoneFragment);
    }

    @Override
    public void GoogleSignUp() {
        Intent signInIntent = viewModel.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, Constants.Google.SIGN_IN_RC);
    }


    @Override
    public void EmailSignUp() {
        addFragment(signUpFragment);
    }

    @Override
    public void PhoneSignUp() {
        addFragment(signUpFragment);
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        Fragment fragment = fragments.get(fragments.size() - 1);
        if(fragment instanceof SignUpFragment || fragment instanceof EmailPhoneFragment){
            removeFragment(fragment);
        }
        else {
            super.onBackPressed();
        }
    }

    private void removeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
