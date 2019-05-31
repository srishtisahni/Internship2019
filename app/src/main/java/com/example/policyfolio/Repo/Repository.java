package com.example.policyfolio.Repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;

import com.example.policyfolio.Data.Facebook;
import com.example.policyfolio.Repo.Facebook.GraphAPI;
import com.example.policyfolio.Repo.Firebase.Authentication;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class Repository {

    private GraphAPI graphAPI;
    public static Repository INSTANCE;
    private Facebook facebook;
    private Authentication authentication;

    private Repository(){
        graphAPI = new GraphAPI();
        authentication = new Authentication();
    }

    public static Repository getINSTANCE() {
        if(INSTANCE==null){
            INSTANCE = new Repository();
        }
        return INSTANCE;
    }

    public void getFacebookProfile(MutableLiveData<Facebook> facebookFetch, AccessToken accessToken) {
        Profile profile = Profile.getCurrentProfile();
        if(facebook !=null){
            if(facebook != null) {
                if(profile.getId().equals(facebook.getId()+"")) {
                    facebookFetch.setValue(facebook);
                    return;
                }
            }
        }
        graphAPI.getFacebookProfile(facebookFetch,accessToken);
    }

    public Facebook getFacebook(long id) {
        if(id == facebook.getId())
            return facebook;
        else
            return null;
    }


    public void initiateGoogleLogin(String id, Context context) {
        authentication.initiateGoogleLogin(id, context);
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return authentication.getGoogleSignInClient();
    }

    public LiveData<FirebaseUser> googleAuthentication(Intent data, Executor context) {
        return authentication.googleAuthentication(data, context);
    }

    public LiveData<FirebaseUser> facebookFirebaseUser(Executor context) {
        return authentication.facebookFirebaseUser(context);
    }
}
