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

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.Facebook;
import com.example.policyfolio.DataClasses.User;
import com.example.policyfolio.R;
import com.example.policyfolio.UI.CallBackListeners.EmailPhoneCallback;
import com.example.policyfolio.UI.CallBackListeners.LoginFragmentCallback;
import com.example.policyfolio.UI.CallBackListeners.SignUpFragmentCallback;
import com.example.policyfolio.UI.Fragments.EmailPhoneFragment;
import com.example.policyfolio.UI.Fragments.LoginFragment;
import com.example.policyfolio.UI.Fragments.SignUpFragment;
import com.example.policyfolio.ViewModels.LoginSignUpViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.util.List;

public class LoginSignUpActivity extends AppCompatActivity implements LoginFragmentCallback, EmailPhoneCallback, SignUpFragmentCallback {

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.Google.SIGN_IN_RC){
            progressBar.setVisibility(View.VISIBLE);
            fragmentHolder.setAlpha(0.4f);
            viewModel.googleAuthentication(data).observe(this, new Observer<FirebaseUser>() {
                @Override
                public void onChanged(@Nullable FirebaseUser firebaseUser) {
                    progressBar.setVisibility(View.GONE);
                    fragmentHolder.setAlpha(1f);
                    if(firebaseUser!=null){
                        addUser(firebaseUser);
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.SharedPreferenceKeys.TYPE, Constants.SharedPreferenceKeys.Type.GOOGLE);
                        startHomeActivity(firebaseUser,bundle);
                    }
                    else {
                        Toast.makeText(LoginSignUpActivity.this,"Google Sign Up Failed",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        loginFragment.onActivityResult(requestCode, resultCode, data);
    }

    private void startHomeActivity(FirebaseUser firebaseUser, Bundle bundle) {
        bundle.putString(Constants.SharedPreferenceKeys.FIREBASE_UID,firebaseUser.getUid());
        bundle.putBoolean(Constants.SharedPreferenceKeys.LOGGED_IN,true);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.SharedPreferenceKeys.LOGGED_IN,true);
        editor.putInt(Constants.SharedPreferenceKeys.TYPE,bundle.getInt(Constants.SharedPreferenceKeys.TYPE));
        editor.putString(Constants.SharedPreferenceKeys.FIREBASE_UID,bundle.getString(Constants.SharedPreferenceKeys.FIREBASE_UID));
        editor.commit();

        Intent intent = new Intent(LoginSignUpActivity.this,HomeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void FacebookSignUp(Facebook facebook) {
        viewModel.setGender(facebook.getGender());
        viewModel.setCity(facebook.getLocationName());
        try {
            viewModel.setBirthDay(Constants.FACEBOOK_DATE_FORMAT.parse(facebook.getBirthday()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            viewModel.setBirthDay(null);
        }
        progressBar.setVisibility(View.VISIBLE);
        fragmentHolder.setAlpha(0.4f);
        viewModel.facebookFirebaseUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable FirebaseUser firebaseUser) {
                progressBar.setVisibility(View.GONE);
                fragmentHolder.setAlpha(1f);
                if(firebaseUser!=null){
                    addUser(firebaseUser);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.SharedPreferenceKeys.TYPE, Constants.SharedPreferenceKeys.Type.FACEBOOK);
                    startHomeActivity(firebaseUser, bundle);
                }
                else {
                    Toast.makeText(LoginSignUpActivity.this,"Facebook Login Failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void enterEmail() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SharedPreferenceKeys.TYPE, Constants.SharedPreferenceKeys.Type.EMAIL);
        emailPhoneFragment.setArguments(bundle);
        addFragment(emailPhoneFragment);
    }

    @Override
    public void enterPhone() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SharedPreferenceKeys.TYPE, Constants.SharedPreferenceKeys.Type.PHONE);
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
        viewModel.logIn().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable FirebaseUser firebaseUser) {
                progressBar.setVisibility(View.GONE);
                fragmentHolder.setAlpha(1f);
                if(firebaseUser!=null){
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.SharedPreferenceKeys.TYPE, Constants.SharedPreferenceKeys.Type.EMAIL);
                    startHomeActivity(firebaseUser, bundle);
                }
                else {
                    Toast.makeText(LoginSignUpActivity.this,"Invalid Email or Password",Toast.LENGTH_LONG).show();
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
    public void EmailSignUp() {
        addFragment(signUpFragment);
    }

    @Override
    public void PhoneSignUp() {
        progressBar.setVisibility(View.VISIBLE);
        fragmentHolder.setAlpha(0.4f);
        viewModel.signUpPhone(this).observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable FirebaseUser firebaseUser) {
                progressBar.setVisibility(View.GONE);
                fragmentHolder.setAlpha(1f);
                if(firebaseUser!=null){
                    addUser(firebaseUser);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.SharedPreferenceKeys.TYPE, Constants.SharedPreferenceKeys.Type.PHONE);
                    startHomeActivity(firebaseUser, bundle);
                }
                else {
                    Toast.makeText(LoginSignUpActivity.this,"Phone Sign Up Failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void SignUp() {
        progressBar.setVisibility(View.VISIBLE);
        fragmentHolder.setAlpha(0.4f);
        viewModel.SignUp().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable FirebaseUser firebaseUser) {
                progressBar.setVisibility(View.GONE);
                fragmentHolder.setAlpha(1f);
                if(firebaseUser!=null){
                    addUser(firebaseUser);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.SharedPreferenceKeys.TYPE, Constants.SharedPreferenceKeys.Type.EMAIL);
                    startHomeActivity(firebaseUser, bundle);
                }
                else {
                    Toast.makeText(LoginSignUpActivity.this,"Email Sign Up Failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addUser(final FirebaseUser firebaseUser) {
        viewModel.fetchUser(firebaseUser.getUid(),this).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user == null){
                    viewModel.updateUserInfo(firebaseUser).observe(LoginSignUpActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(@Nullable Boolean aBoolean) {
                            if(!aBoolean)
                                Toast.makeText(LoginSignUpActivity.this,"Information couldn't be updated.",Toast.LENGTH_LONG).show();
                        }
                    });
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
