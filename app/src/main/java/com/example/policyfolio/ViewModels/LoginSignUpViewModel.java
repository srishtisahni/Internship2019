package com.example.policyfolio.ViewModels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;

import com.example.policyfolio.Constants;
import com.example.policyfolio.Data.Facebook;
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

import java.util.concurrent.Executor;

public class LoginSignUpViewModel extends ViewModel implements FragmentViewModelCallback {

    private CallbackManager callbackManager;
    private Repository repository = Repository.getINSTANCE();

    private MutableLiveData<Facebook> facebookFetch = new MutableLiveData<>();
    private MutableLiveData<Integer> facebookLoginStatus = new MutableLiveData<>();

    private String email;
    private Long phone;
    private Integer type;


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

    public void logOutFacebook() {
        LoginManager.getInstance().logOut();
    }

    public void initiateGoogleLogin(String id, Context context) {
        repository.initiateGoogleLogin(id, context);
    }

    public Integer emailPhoneUpdate(String text) {
        try{
            Long phone = Long.parseLong(text);
            this.phone =phone;
            this.type = Constants.Login.Type.PHONE;
        }
        catch (Exception e){
            e.printStackTrace();
            this.email = text;
            this.type = Constants.Login.Type.EMAIL;
        }
        return type;
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

    public void updateRepoUser(FirebaseUser firebaseUser) {
        repository.updateFirebaseUser(firebaseUser);
    }

    public void UpdateRepoFacebook(Facebook facebook) {
        repository.setFacebook(facebook);
    }

    public LiveData<FirebaseUser> signUpPhone(Activity activity) {
        return repository.phoneSignUp(phone, activity);
    }
}
