package com.example.policyfolio.Repo;

import android.arch.lifecycle.MutableLiveData;

import com.example.policyfolio.Data.Facebook;
import com.example.policyfolio.Repo.Facebook.GraphAPI;
import com.facebook.AccessToken;
import com.facebook.Profile;

public class Repository {

    private GraphAPI graphAPI;
    public static Repository INSTANCE;
    private Facebook facebook;

    private Repository(){
        graphAPI = new GraphAPI();
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

    public void setFacebook(Facebook facebook) {
        this.facebook = facebook;
    }
}
