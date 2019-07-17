package com.example.policyfolio.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.policyfolio.data.facebook.dataclasses.FacebookData;
import com.example.policyfolio.util.Constants;
import com.example.policyfolio.data.local.classes.User;
import com.example.policyfolio.ui.login.FragmentViewModelCallback;
import com.example.policyfolio.viewmodels.base.BasicLoginViewModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;

public class LoginSignUpViewModel extends BasicLoginViewModel implements FragmentViewModelCallback {

    private CallbackManager callbackManager;

    private MutableLiveData<Integer> facebookLoginStatus = new MutableLiveData<>();
    private LiveData<FacebookData> facebookData;

    private String email;
    private String phone;
    private String name;
    private long birthdayEpoch = 0;
    private int gender;
    private String city;
    private String password;

    public LiveData<FacebookData> fetchFacebookData() {
        if (facebookData == null)
            facebookData = getRepository().getFacebookProfile(AccessToken.getCurrentAccessToken());
        return facebookData;
    }

    public MutableLiveData<Integer> facebookLogin() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                        if(isLoggedIn) {
                            facebookLoginStatus.setValue(Constants.Facebook.Login.LOGGED_IN);
                        }
                        else
                            facebookLoginStatus.setValue(Constants.Facebook.Login.LOGIN_FAILED);
                    }

                    @Override
                    public void onCancel() {
                        facebookLoginStatus.setValue(Constants.Facebook.Login.LOGIN_CANCELLED);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        facebookLoginStatus.setValue(Constants.Facebook.Login.LOGIN_ERROR);
                        exception.printStackTrace();
                    }
                });
        return facebookLoginStatus;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    public void initiateGoogleLogin(String id, Context context) {
        getRepository().initiateGoogleLogin(id, context);
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return getRepository().getGoogleSignInClient();
    }

    public LiveData<FirebaseUser> googleAuthentication(Intent data) {
        return getRepository().googleAuthentication(data);
    }

    public LiveData<FirebaseUser> facebookFirebaseUser() {
        return getRepository().facebookFirebaseUser();
    }

    public LiveData<Boolean> updateUserInfo(FirebaseUser firebaseUser,Integer type) {
        if(email == null)
            email = firebaseUser.getEmail();
        if(phone == null)
            phone = firebaseUser.getPhoneNumber();
        if(name == null)
            name = firebaseUser.getDisplayName();
        User user = new User(firebaseUser.getUid(),email,phone,name, birthdayEpoch,gender,city,type);
        return getRepository().updateFirebaseUser(user);
    }

    public LiveData<FirebaseUser> signUpPhoneWithOTP(Activity activity) {
        return getRepository().phoneSignUp(phone, activity);
    }

    public LiveData<FirebaseUser> signUpPhoneWithOTP(String otp) {
        return getRepository().phoneSignUp(otp);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDay(Long birthdayEpoch) {
        this.birthdayEpoch = birthdayEpoch;
    }

    public void setGender(int genderSelection) {
        this.gender = genderSelection;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LiveData<FirebaseUser> SignUp() {
        return getRepository().SignUp(email,password);
    }

    public String getEmail() {
        return email;
    }

    public LiveData<FirebaseUser> logIn() {
        return getRepository().Login(email,password);
    }

    public LiveData<Integer> checkIfUserExistsEmail(Intent data) {
        return getRepository().checkIfUserExistsEmail(data);
    }

    public LiveData<Integer> checkIfUserExistsEmail(String email) {
        return getRepository().checkIfUserExistsEmail(email);
    }

    public LiveData<Integer> checkIfUserExistsEmail() {
        return getRepository().checkIfUserExistsEmail(email);
    }

    public LiveData<Integer> checkIfUserExistsPhone() {
        return getRepository().checkIfUserExistsPhone(phone);
    }

    public String getPhone() {
        return phone;
    }

    public LiveData<Boolean> resetPassword() {
        return getRepository().resetPassword(email);
    }

}
