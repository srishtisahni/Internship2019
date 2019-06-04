package com.example.policyfolio.ViewModels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.Facebook;
import com.example.policyfolio.DataClasses.User;
import com.example.policyfolio.Repo.Repository;
import com.example.policyfolio.UI.CallBackListeners.FragmentViewModelCallback;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;

public class LoginSignUpViewModel extends ViewModel implements FragmentViewModelCallback {

    private CallbackManager callbackManager;
    private Repository repository = Repository.getInstance();

    private MutableLiveData<Facebook> facebookFetch = new MutableLiveData<>();
    private MutableLiveData<Integer> facebookLoginStatus = new MutableLiveData<>();

    private String email;
    private String phone;
    private Integer type;
    private String name;
    private Long birthdatEpoch;
    private int gender;
    private String city;
    private String password;


    public void fetchFacebookData(AccessToken accessToken) {
        repository.getFacebookProfile(facebookFetch,accessToken);
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
                            fetchFacebookData(accessToken);
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

    public MutableLiveData<Facebook> getFacebookFetch() {
        return facebookFetch;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    public void initiateGoogleLogin(String id, Context context) {
        repository.initiateGoogleLogin(id, context);
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return repository.getGoogleSignInClient();
    }

    public LiveData<FirebaseUser> googleAuthentication(Intent data) {
        return repository.googleAuthentication(data);
    }

    public LiveData<FirebaseUser> facebookFirebaseUser() {
        return repository.facebookFirebaseUser();
    }

    public LiveData<Boolean> updateUserInfo(FirebaseUser firebaseUser) {
        User user = new User(firebaseUser.getUid(),firebaseUser.getEmail(),firebaseUser.getPhoneNumber(),firebaseUser.getDisplayName(),birthdatEpoch,gender,city);
        Log.e(firebaseUser.toString(),user.toString());
        return repository.updateFirebaseUser(firebaseUser,user);
    }

    public void UpdateRepoFacebook(Facebook facebook) {
        repository.setFacebook(facebook);
    }

    public LiveData<FirebaseUser> signUpPhone(Activity activity) {
        return repository.phoneSignUp(phone, activity);
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
        this.birthdatEpoch = birthdayEpoch;
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
        return repository.SignUp(email,password);
    }

    public String getEmail() {
        return email;
    }

    public LiveData<FirebaseUser> logIn() {
        return repository.Login(email,password);
    }
}
