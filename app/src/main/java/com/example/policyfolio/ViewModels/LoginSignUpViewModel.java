package com.example.policyfolio.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.policyfolio.Data.Facebook;
import com.example.policyfolio.Repo.Repository;
import com.facebook.AccessToken;

public class LoginSignUpViewModel extends ViewModel {

    private MutableLiveData<Facebook> facebookFetch = new MutableLiveData<>();
    private Repository repository = Repository.getINSTANCE();
    public MutableLiveData<Facebook> fetchFacebookData(AccessToken accessToken) {
        repository.getFacebookProfile(facebookFetch,accessToken);
        return facebookFetch;
    }
}
