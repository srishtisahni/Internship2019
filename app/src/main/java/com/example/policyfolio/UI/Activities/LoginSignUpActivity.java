package com.example.policyfolio.UI.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.policyfolio.Constants;
import com.example.policyfolio.Data.Facebook;
import com.example.policyfolio.R;
import com.example.policyfolio.UI.CallBackListeners.LoginFragmentCallback;
import com.example.policyfolio.UI.Fragments.LoginFragment;
import com.example.policyfolio.ViewModels.LoginSignUpViewModel;

public class LoginSignUpActivity extends AppCompatActivity implements LoginFragmentCallback {

    LoginFragment fragment;
    LoginSignUpViewModel viewModel;
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

        fragment = new LoginFragment(this,this);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder, fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void SignUp(Facebook facebook) {
        Toast.makeText(this,facebook.getName(), Toast.LENGTH_LONG).show();
    }
}
