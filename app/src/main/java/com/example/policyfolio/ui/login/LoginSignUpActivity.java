package com.example.policyfolio.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.policyfolio.data.facebook.dataclasses.FacebookData;
import com.example.policyfolio.data.firebase.classes.LogInData;
import com.example.policyfolio.ui.base.BasicProgressActivity;
import com.example.policyfolio.ui.bottomsheets.EmailBottomSheet;
import com.example.policyfolio.ui.bottomsheets.EmailSheetCallback;
import com.example.policyfolio.ui.bottomsheets.ListBottomSheet;
import com.example.policyfolio.ui.home.HomeActivity;
import com.example.policyfolio.util.Constants;
import com.example.policyfolio.R;
import com.example.policyfolio.viewmodels.LoginSignUpViewModel;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.util.List;

public class LoginSignUpActivity extends BasicProgressActivity implements LoginCallback, WelcomeCallback {

    private LoginSignUpViewModel viewModel;

    private LoginFragment loginFragment;
    private SignUpFragment signUpFragment;
    private EmailPhoneFragment emailPhoneFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getBooleanExtra(Constants.LoginInInfo.POST_LOGOUT,false)) {
            setTheme(R.style.AppTheme_NoActionBar);
            setContentView(R.layout.activity_login_signup);
            openLoginSignUp();
        }
        else {
            setTheme(R.style.AppTheme_NoActionBar_fullscreen);
            setContentView(R.layout.activity_login_signup);
            setUpWelcome();
        }

        viewModel = ViewModelProviders.of(this).get(LoginSignUpViewModel.class);
        viewModel.initiateRepo(this);
    }

    private void setUpWelcome() {
        super.setUpFullScreen();
        WelcomeFragment welcomeFragment = new WelcomeFragment(this,getSharedPreferences(Constants.LOGIN_SHARED_PREFERENCE_KEY,MODE_PRIVATE));
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder, welcomeFragment).commit();
    }

    @Override
    public void openLoginSignUp() {
        Drawable dr = getResources().getDrawable(R.drawable.titlebar_icon);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(),
                Bitmap.createScaledBitmap(bitmap, 120, 120, true));
        getSupportActionBar().setIcon(d);

        loginFragment = new LoginFragment(this);
        signUpFragment = new SignUpFragment(this);
        emailPhoneFragment = new EmailPhoneFragment(this);

        addFragment(loginFragment);
        super.disableFullscreen();
    }

    @Override
    public void openHome(LogInData logInData) {
        Intent intent = new Intent(this, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.LoginInInfo.LOGGED_IN, logInData.isLogin());
        bundle.putInt(Constants.LoginInInfo.TYPE, logInData.getType());
        bundle.putString(Constants.LoginInInfo.FIREBASE_UID, logInData.getFirebaseToken());
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
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
                                        endProgress();
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
                                        endProgress();
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
        EmailBottomSheet emailBottomSheet = new EmailBottomSheet(new EmailSheetCallback() {
            @Override
            public void ForgotPassword(String s) {
                startProgress();
                viewModel.setEmail(s);
                viewModel.resetPassword().observe(LoginSignUpActivity.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean aBoolean) {
                        endProgress();
                        LoginSignUpActivity.super.collapseSheet();
                        if(aBoolean) {
                            showSnackbar("A Password Reset email has been sent to your email Id");
                        }
                        else {
                            showSnackbar("The account doesn't exist");
                        }
                    }
                });
            }

            @Override
            public void startProgress() {
                LoginSignUpActivity.super.startSheetProgress();
            }

            @Override
            public void endProgress() {
                LoginSignUpActivity.super.endSheetProgress();
            }
        });
        Bundle args = new Bundle();
        args.putString(Constants.User.EMAIL,viewModel.getEmail());
        emailBottomSheet.setArguments(args);
        super.expandSheet(emailBottomSheet);
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
                                        endProgress();
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
        if(super.isSheetOpen()){
            super.collapseSheet();
        }
        else {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            Fragment fragment = fragments.get(fragments.size() - 1);
            if(fragment instanceof SignUpFragment || fragment instanceof EmailPhoneFragment){
                removeFragment(fragment);
            }
            else {
                super.onBackPressed();
            }
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
                                        endProgress();
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
                                        endProgress();
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

    @Override
    public void openListSheet(int type, RecyclerView.Adapter adapter) {
        ListBottomSheet listBottomSheet = new ListBottomSheet(type,adapter);
        super.expandSheet(listBottomSheet);
    }

    @Override
    public void closeListSheet() {
        super.collapseSheet();
    }

    @Override
    public void setBackgroundToGreen() {
        super.setFragmentHolderBg(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void setBackgroundToWhite() {
        super.setFragmentHolderBg(getResources().getColor(R.color.white));
    }
}
