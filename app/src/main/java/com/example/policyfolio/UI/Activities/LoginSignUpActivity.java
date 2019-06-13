package com.example.policyfolio.UI.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.policyfolio.Repo.Facebook.DataClasses.FacebookData;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.R;
import com.example.policyfolio.Util.CallBackListeners.LoginCallback;
import com.example.policyfolio.UI.Fragments.EmailPhoneFragment;
import com.example.policyfolio.UI.Fragments.LoginFragment;
import com.example.policyfolio.UI.Fragments.SignUpFragment;
import com.example.policyfolio.ViewModels.LoginSignUpViewModel;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.util.List;

public class LoginSignUpActivity extends AppCompatActivity implements LoginCallback {

    private LoginFragment loginFragment;
    private SignUpFragment signUpFragment;
    private EmailPhoneFragment emailPhoneFragment;
    private LoginSignUpViewModel viewModel;

    private FrameLayout fragmentHolder;
    private ProgressBar progressBar;

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
        viewModel.initiateRepo(this);

        fragmentHolder = findViewById(R.id.fragment_holder);
        progressBar = findViewById(R.id.progress_bar);

        loginFragment = new LoginFragment(this);
        signUpFragment = new SignUpFragment(this);
        emailPhoneFragment = new EmailPhoneFragment(this);

        addFragment(loginFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.Google.SIGN_IN_RC){
            progressBar.setVisibility(View.VISIBLE);
            fragmentHolder.setAlpha(0.4f);
            viewModel.checkIfUserExistsEmail(data).observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if(integer!=null){
                        switch (integer){
                            case Constants.LoginInInfo.Type.GOOGLE:
                                viewModel.googleAuthentication(data).observe(LoginSignUpActivity.this, new Observer<FirebaseUser>() {
                                    @Override
                                    public void onChanged(@Nullable FirebaseUser firebaseUser) {
                                        if(firebaseUser!=null){
                                            addUser(firebaseUser,Constants.LoginInInfo.Type.GOOGLE);
                                        }
                                        else {
                                            Toast.makeText(LoginSignUpActivity.this,"Google Sign Up Failed",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                break;
                            case Constants.LoginInInfo.Type.FACEBOOK:
                                progressBar.setVisibility(View.GONE);
                                fragmentHolder.setAlpha(1f);
                                Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using Facebook",Toast.LENGTH_LONG).show();
                                break;
                            case Constants.LoginInInfo.Type.PHONE:
                                progressBar.setVisibility(View.GONE);
                                fragmentHolder.setAlpha(1f);
                                Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using your Phone Number",Toast.LENGTH_LONG).show();
                                break;
                            case Constants.LoginInInfo.Type.EMAIL:
                                progressBar.setVisibility(View.GONE);
                                fragmentHolder.setAlpha(1f);
                                Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using your Email and Password",Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                    else {
                        Toast.makeText(LoginSignUpActivity.this,"Please check your internet connection and try again",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        loginFragment.onActivityResult(requestCode, resultCode, data);
    }

    private void startHomeActivity(FirebaseUser firebaseUser, int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LoginInInfo.TYPE, type);
        bundle.putString(Constants.LoginInInfo.FIREBASE_UID,firebaseUser.getUid());
        bundle.putBoolean(Constants.LoginInInfo.LOGGED_IN,true);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.LoginInInfo.LOGGED_IN,true);
        editor.putInt(Constants.LoginInInfo.TYPE,bundle.getInt(Constants.LoginInInfo.TYPE));
        editor.putString(Constants.LoginInInfo.FIREBASE_UID,bundle.getString(Constants.LoginInInfo.FIREBASE_UID));
        editor.apply();

        Intent intent = new Intent(LoginSignUpActivity.this,HomeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void FacebookSignUp(final FacebookData facebookData) {
        viewModel.setGender(facebookData.getGender());
        viewModel.setCity(facebookData.getLocationName());
        try {
            viewModel.setBirthDay(Constants.Facebook.DATE_FORMAT.parse(facebookData.getBirthday()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            viewModel.setBirthDay(null);
        }
        progressBar.setVisibility(View.VISIBLE);
        fragmentHolder.setAlpha(0.4f);
        viewModel.checkIfUserExistsEmail(facebookData.getEmail(),Constants.LoginInInfo.Type.FACEBOOK).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=null){
                    switch (integer){
                        case Constants.LoginInInfo.Type.GOOGLE:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            LoginManager.getInstance().logOut();
                            Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using Google",Toast.LENGTH_LONG).show();
                            break;
                        case Constants.LoginInInfo.Type.FACEBOOK:
                            viewModel.facebookFirebaseUser().observe(LoginSignUpActivity.this, new Observer<FirebaseUser>() {
                                @Override
                                public void onChanged(@Nullable FirebaseUser firebaseUser) {
                                    if(firebaseUser!=null){
                                        addUser(firebaseUser,Constants.LoginInInfo.Type.FACEBOOK);
                                    }
                                    else {
                                        Toast.makeText(LoginSignUpActivity.this,"Facebook Login Failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            break;
                        case Constants.LoginInInfo.Type.PHONE:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            LoginManager.getInstance().logOut();
                            Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using your Phone Number",Toast.LENGTH_LONG).show();
                            break;
                        case Constants.LoginInInfo.Type.EMAIL:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            LoginManager.getInstance().logOut();
                            Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using your Email and Password",Toast.LENGTH_LONG).show();
                            break;
                    }
                }
                else {
                    Toast.makeText(LoginSignUpActivity.this,"Please check your internet connection and try again",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void enterEmail() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LoginInInfo.TYPE, Constants.LoginInInfo.Type.EMAIL);
        emailPhoneFragment.setArguments(bundle);
        addFragment(emailPhoneFragment);
    }

    @Override
    public void enterPhone() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LoginInInfo.TYPE, Constants.LoginInInfo.Type.PHONE);
        emailPhoneFragment.setArguments(bundle);
        addFragment(emailPhoneFragment);
    }

    @Override
    public void GoogleSignUp() {
        Intent signInIntent = viewModel.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, Constants.Google.SIGN_IN_RC);
    }

    @Override
    public void Login() {
        progressBar.setVisibility(View.VISIBLE);
        fragmentHolder.setAlpha(0.4f);
        viewModel.checkIfUserExistsEmail(Constants.LoginInInfo.Type.EMAIL).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=null){
                    switch (integer){
                        case Constants.LoginInInfo.Type.GOOGLE:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using Google",Toast.LENGTH_LONG).show();
                            break;
                        case Constants.LoginInInfo.Type.FACEBOOK:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using Facebook",Toast.LENGTH_LONG).show();
                            break;
                        case Constants.LoginInInfo.Type.PHONE:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using your Phone Number",Toast.LENGTH_LONG).show();
                            break;
                        case Constants.LoginInInfo.Type.EMAIL:
                            viewModel.logIn().observe(LoginSignUpActivity.this, new Observer<FirebaseUser>() {
                                @Override
                                public void onChanged(@Nullable FirebaseUser firebaseUser) {
                                    progressBar.setVisibility(View.GONE);
                                    fragmentHolder.setAlpha(1f);
                                    if(firebaseUser!=null){
                                        startHomeActivity(firebaseUser, Constants.LoginInInfo.Type.EMAIL);
                                    }
                                    else {
                                        Toast.makeText(LoginSignUpActivity.this,"Invalid Email or Password",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            break;
                    }
                }
                else {
                    Toast.makeText(LoginSignUpActivity.this,"Please check your internet connection and try again",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void forgotPassword() {
        Intent intent = new Intent(this,PopUpActivity.class);
        intent.putExtra(Constants.PopUps.POPUP_TYPE,Constants.PopUps.Type.EMAIL_POPUP);
        intent.putExtra(Constants.User.EMAIL,viewModel.getEmail());
        startActivity(intent);
    }


    @Override
    public void EmailNext() {
        progressBar.setVisibility(View.VISIBLE);
        fragmentHolder.setAlpha(0.4f);
        viewModel.checkIfUserExistsEmail(Constants.LoginInInfo.Type.EMAIL).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=null){
                    switch (integer){
                        case Constants.LoginInInfo.Type.GOOGLE:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using Google",Toast.LENGTH_LONG).show();
                            break;
                        case Constants.LoginInInfo.Type.FACEBOOK:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using Facebook",Toast.LENGTH_LONG).show();
                            break;
                        case Constants.LoginInInfo.Type.PHONE:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using your Phone Number",Toast.LENGTH_LONG).show();
                            break;
                        case Constants.LoginInInfo.Type.EMAIL:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            addFragment(signUpFragment);
                            break;
                    }
                }
                else {
                    Toast.makeText(LoginSignUpActivity.this,"Please check your internet connection and try again",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void PhoneSignUp() {
        progressBar.setVisibility(View.VISIBLE);
        fragmentHolder.setAlpha(0.4f);
        viewModel.checkIfUserExistsPhone().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=null){
                    switch (integer){
                        case Constants.LoginInInfo.Type.GOOGLE:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using Google",Toast.LENGTH_LONG).show();
                            break;
                        case Constants.LoginInInfo.Type.FACEBOOK:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using Facebook",Toast.LENGTH_LONG).show();
                            break;
                        case Constants.LoginInInfo.Type.PHONE:
                            viewModel.signUpPhone(LoginSignUpActivity.this).observe(LoginSignUpActivity.this, new Observer<FirebaseUser>() {
                                @Override
                                public void onChanged(@Nullable FirebaseUser firebaseUser) {
                                    if(firebaseUser!=null){
                                        addUser(firebaseUser,Constants.LoginInInfo.Type.PHONE);
                                    }
                                    else {
                                        Toast.makeText(LoginSignUpActivity.this,"Phone Sign Up Failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            break;
                        case Constants.LoginInInfo.Type.EMAIL:
                            progressBar.setVisibility(View.GONE);
                            fragmentHolder.setAlpha(1f);
                            Toast.makeText(LoginSignUpActivity.this,"Account already Exists. Please Sign In using your Email and Password",Toast.LENGTH_LONG).show();
                            break;
                    }
                }
                else {
                    Toast.makeText(LoginSignUpActivity.this,"Please check your internet connection and try again",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void SignUpEmailAndPassword() {
        progressBar.setVisibility(View.VISIBLE);
        fragmentHolder.setAlpha(0.4f);
        viewModel.SignUp().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable FirebaseUser firebaseUser) {
                if(firebaseUser!=null){
                    addUser(firebaseUser,Constants.LoginInInfo.Type.EMAIL);
                }
                else {
                    Toast.makeText(LoginSignUpActivity.this,"Email Sign Up Failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addUser(final FirebaseUser firebaseUser, final Integer type) {
        viewModel.updateUserInfo(firebaseUser,type).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                progressBar.setVisibility(View.GONE);
                fragmentHolder.setAlpha(1f);
                if(!aBoolean)
                    Toast.makeText(LoginSignUpActivity.this,"Information couldn't be updated.",Toast.LENGTH_LONG).show();
                else {
                    startHomeActivity(firebaseUser, type);
                }
            }
        });
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
