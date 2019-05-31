package com.example.policyfolio.ViewModels;

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
    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<Long> phone =new MutableLiveData<>();
    private MutableLiveData<Integer> type = new MutableLiveData<>();


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

    public MutableLiveData<Integer> emailPhoneUpdate(String text) {
        try{
            Long phone = Long.parseLong(text);
            this.phone.setValue(phone);
            this.email.setValue(null);
            this.type.setValue(Constants.Login.Type.PHONE);
        }
        catch (Exception e){
            e.printStackTrace();
            this.email.setValue(text);
            this.phone.setValue(null);
            this.type.setValue(Constants.Login.Type.EMAIL);
        }
        return type;
    }

    public void setType(int type) {
        this.type.setValue(type);
    }

    public MutableLiveData<Integer> getType() {
        return type;
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return repository.getGoogleSignInClient();
    }

    public LiveData<FirebaseUser> googleAuthentication(Intent data, Executor context) {
        return repository.googleAuthentication(data, context);
    }

    public LiveData<FirebaseUser> facebookFirebaseUser(Executor context) {
        return repository.facebookFirebaseUser(context);
    }
}
