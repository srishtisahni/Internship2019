package com.example.policyfolio.UI.LoginSignUp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.policyfolio.Data.Facebook.DataClasses.FacebookData;
import com.example.policyfolio.UI.Base.BasicProgressActivity;
import com.example.policyfolio.UI.Home.HomeActivity;
import com.example.policyfolio.UI.PopUps.PopUpActivity;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.R;
import com.example.policyfolio.ViewModels.LoginSignUpViewModel;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.util.List;

public class LoginSignUpActivity extends BasicProgressActivity implements LoginCallback {

    private LoginSignUpViewModel viewModel;

    private LoginFragment loginFragment;
    private SignUpFragment signUpFragment;
    private EmailPhoneFragment emailPhoneFragment;

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

        loginFragment = new LoginFragment(this);
        signUpFragment = new SignUpFragment(this);
        emailPhoneFragment = new EmailPhoneFragment(this);

        addFragment(loginFragment);
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

        Intent intent = new Intent(LoginSignUpActivity.this, HomeActivity.class);
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
        startProgress();
        viewModel.checkIfUserExistsEmail(facebookData.getEmail()).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=null){
                    switch (integer){
                        case Constants.LoginInInfo.Type.GOOGLE:
                            endProgress();
                            LoginManager.getInstance().logOut();
                            showSnackbar("Account already Exists. Please Sign In using Google");
                            break;
                        case Constants.LoginInInfo.Type.FACEBOOK:
                            viewModel.facebookFirebaseUser().observe(LoginSignUpActivity.this, new Observer<FirebaseUser>() {
                                @Override
                                public void onChanged(@Nullable FirebaseUser firebaseUser) {
                                    if(firebaseUser!=null){
                                        startHomeActivity(firebaseUser,Constants.LoginInInfo.Type.FACEBOOK);
                                    }
                                    else {
                                        showSnackbar("Facebook Login Failed");
                                    }
                                }
                            });
                            break;
                        case Constants.LoginInInfo.Type.PHONE:
                            endProgress();
                            LoginManager.getInstance().logOut();
                            showSnackbar("Account already Exists. Please Sign In using your Phone Number");
                            break;
                        case Constants.LoginInInfo.Type.EMAIL:
                            endProgress();
                            LoginManager.getInstance().logOut();
                            showSnackbar("Account already Exists. Please Sign In using your Email and Password");
                            break;
                        default:
                            viewModel.facebookFirebaseUser().observe(LoginSignUpActivity.this, new Observer<FirebaseUser>() {
                                @Override
                                public void onChanged(@Nullable FirebaseUser firebaseUser) {
                                    if(firebaseUser!=null){
                                        addUser(firebaseUser,Constants.LoginInInfo.Type.FACEBOOK);
                                    }
                                    else {
                                        showSnackbar("Facebook Login Failed");
                                    }
                                }
                            });
                            break;
                    }
                }
                else {
                    endProgress();
                    showSnackbar("Unable to update Information");
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
        startProgress();
        viewModel.checkIfUserExistsEmail().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=null){
                    switch (integer){
                        case Constants.LoginInInfo.Type.GOOGLE:
                            endProgress();
                            showSnackbar("Account already Exists. Please Sign In using Google");
                            break;
                        case Constants.LoginInInfo.Type.FACEBOOK:
                            endProgress();
                            showSnackbar("Account already Exists. Please Sign In using Facebook");
                            break;
                        case Constants.LoginInInfo.Type.PHONE:
                            endProgress();
                            showSnackbar("Account already Exists. Please Sign In using your Phone Number");
                            break;
                        case Constants.LoginInInfo.Type.EMAIL:
                            viewModel.logIn().observe(LoginSignUpActivity.this, new Observer<FirebaseUser>() {
                                @Override
                                public void onChanged(@Nullable FirebaseUser firebaseUser) {
                                    endProgress();
                                    if(firebaseUser!=null){
                                        startHomeActivity(firebaseUser, Constants.LoginInInfo.Type.EMAIL);
                                    }
                                    else {
                                        showSnackbar("Incorrect Password");
                                    }
                                }
                            });
                            break;
                        default:
                            endProgress();
                            showSnackbar("The account does not Exist");
                            break;
                    }
                }
                else {
                    endProgress();
                    showSnackbar("Unable to update Information");
                }
            }
        });
    }

    @Override
    public void forgotPassword() {
        Intent intent = new Intent(this, PopUpActivity.class);
        intent.putExtra(Constants.PopUps.POPUP_TYPE,Constants.PopUps.Type.EMAIL_POPUP);
        intent.putExtra(Constants.User.EMAIL,viewModel.getEmail());
        startActivity(intent);
    }


    @Override
    public void EmailNext() {
        startProgress();
        viewModel.checkIfUserExistsEmail().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=null){
                    switch (integer){
                        case Constants.LoginInInfo.Type.GOOGLE:
                            endProgress();
                            showSnackbar("Account already Exists. Please Sign In using Google");
                            break;
                        case Constants.LoginInInfo.Type.FACEBOOK:
                            endProgress();
                            showSnackbar("Account already Exists. Please Sign In using Facebook");
                            break;
                        case Constants.LoginInInfo.Type.PHONE:
                            endProgress();
                            showSnackbar("Account already Exists. Please Sign In using your Phone Number");
                            break;
                        case Constants.LoginInInfo.Type.EMAIL:
                            endProgress();
                            showSnackbar("Account already Exists. Please Sign In using your Email and Password");
                            break;
                        default:
                            endProgress();
                            addFragment(signUpFragment);
                            break;
                    }
                }
                else {
                    endProgress();
                    showSnackbar("Unable to update Information");
                }
            }
        });
    }

    @Override
    public void PhoneSignUp() {
        startProgress();
        viewModel.checkIfUserExistsPhone().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=null){
                    switch (integer){
                        case Constants.LoginInInfo.Type.GOOGLE:
                            endProgress();
                            showSnackbar("Account already Exists. Please Sign In using Google");
                            break;
                        case Constants.LoginInInfo.Type.FACEBOOK:
                            endProgress();
                            showSnackbar("Account already Exists. Please Sign In using Facebook");
                            break;
                        case Constants.LoginInInfo.Type.PHONE:
                            viewModel.signUpPhone(LoginSignUpActivity.this).observe(LoginSignUpActivity.this, new Observer<FirebaseUser>() {
                                @Override
                                public void onChanged(@Nullable FirebaseUser firebaseUser) {
                                    if(firebaseUser!=null){
                                        startHomeActivity(firebaseUser,Constants.LoginInInfo.Type.PHONE);
                                    }
                                    else {
                                        showSnackbar("Phone Sign Up Failed");
                                    }
                                }
                            });
                            break;
                        case Constants.LoginInInfo.Type.EMAIL:
                            endProgress();
                            showSnackbar("Account already Exists. Please Sign In using your Email and Password");
                            break;
                        default:
                            viewModel.signUpPhone(LoginSignUpActivity.this).observe(LoginSignUpActivity.this, new Observer<FirebaseUser>() {
                                @Override
                                public void onChanged(@Nullable FirebaseUser firebaseUser) {
                                    if(firebaseUser!=null){
                                        addUser(firebaseUser,Constants.LoginInInfo.Type.PHONE);
                                    }
                                    else {
                                        showSnackbar("Phone Sign Up Failed");
                                    }
                                }
                            });
                            break;
                    }
                }
                else {
                    endProgress();
                    showSnackbar("SignUp Failed");
                }
            }
        });
    }


    @Override
    public void SignUpEmailAndPassword() {
        startProgress();
        viewModel.SignUp().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable FirebaseUser firebaseUser) {
                if(firebaseUser!=null){
                    addUser(firebaseUser,Constants.LoginInInfo.Type.EMAIL);
                }
                else {
                    endProgress();
                    showSnackbar("Email Sign Up Failed");
                }
            }
        });
    }

    private void addUser(final FirebaseUser firebaseUser, final Integer type) {
        viewModel.updateUserInfo(firebaseUser,type).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                endProgress();
                if(!aBoolean)
                    showSnackbar("Unable to update Information.");
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
    protected void finalize() throws Throwable {
        super.finalize();
    }

    private void removeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.Google.SIGN_IN_RC){
            startProgress();
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
                                            startHomeActivity(firebaseUser,Constants.LoginInInfo.Type.GOOGLE);
                                        }
                                        else {
                                            showSnackbar("Google Sign Up Failed");
                                        }
                                    }
                                });
                                break;
                            case Constants.LoginInInfo.Type.FACEBOOK:
                                endProgress();
                                showSnackbar("Account already Exists. Please Sign In using Facebook");
                                break;
                            case Constants.LoginInInfo.Type.PHONE:
                                endProgress();
                                showSnackbar("Account already Exists. Please Sign In using your Phone Number");
                                break;
                            case Constants.LoginInInfo.Type.EMAIL:
                                endProgress();
                                showSnackbar("Account already Exists. Please Sign In using your Email and Password");
                                break;
                            default:
                                viewModel.googleAuthentication(data).observe(LoginSignUpActivity.this, new Observer<FirebaseUser>() {
                                    @Override
                                    public void onChanged(@Nullable FirebaseUser firebaseUser) {
                                        if(firebaseUser!=null){
                                            addUser(firebaseUser,Constants.LoginInInfo.Type.GOOGLE);
                                        }
                                        else {
                                            showSnackbar("Google Sign Up Failed");
                                        }
                                    }
                                });
                                break;
                        }
                    }
                    else {
                        endProgress();
                        showSnackbar("Unable to update Information");
                    }
                }
            });
        }
        loginFragment.onActivityResult(requestCode, resultCode, data);
    }
}
