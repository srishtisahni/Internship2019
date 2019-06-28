package com.example.policyfolio.ViewModels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Repo.Facebook.DataClasses.FacebookData;
import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Repo.Database.DataClasses.User;
import com.example.policyfolio.Repo.Repository;
import com.example.policyfolio.Util.CallBackListeners.FragmentViewModelCallback;
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
    private Repository repository;

    private MutableLiveData<Integer> facebookLoginStatus = new MutableLiveData<>();
    private LiveData<FacebookData> facebookData;

    private String email;
    private String phone;
    private String name;
    private long birthdayEpoch = 0;
    private int gender;
    private String city;
    private String password;

    public void initiateRepo(Context context) {
        repository = Repository.getInstance(context);
    }

    public LiveData<FacebookData> fetchFacebookData() {
        if (facebookData == null)
            facebookData = repository.getFacebookProfile(AccessToken.getCurrentAccessToken());
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
        repository.initiateGoogleLogin(id, context);
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

    public LiveData<Boolean> updateUserInfo(FirebaseUser firebaseUser,Integer type) {
        User user = new User(firebaseUser.getUid(),firebaseUser.getEmail(),firebaseUser.getPhoneNumber(),firebaseUser.getDisplayName(), birthdayEpoch,gender,city,type);
        Log.e(firebaseUser.toString(),user.toString());
        return repository.updateFirebaseUser(user);
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
        return repository.SignUp(email,password);
    }

    public String getEmail() {
        return email;
    }

    public LiveData<FirebaseUser> logIn() {
        return repository.Login(email,password);
    }

    public LiveData<Integer> checkIfUserExistsEmail(Intent data) {
        return repository.checkIfUserExistsEmail(data);
    }

    public LiveData<Integer> checkIfUserExistsEmail(String email) {
        return repository.checkIfUserExistsEmail(email);
    }

    public LiveData<Integer> checkIfUserExistsEmail() {
        return repository.checkIfUserExistsEmail(email);
    }

    public LiveData<Integer> checkIfUserExistsPhone() {
        return repository.checkIfUserExistsPhone(phone);
    }
}
