package com.example.policyfolio.ViewModels;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.LoggedIn;
import com.example.policyfolio.Repo.Repository;

public class WelcomeViewModel extends ViewModel {

    private MutableLiveData<LoggedIn> userLoggedIn = new MutableLiveData<>();
    private Repository repository;

    public void initiateRepo(Context context) {
        repository = Repository.getInstance(context);
    }

    public MutableLiveData<LoggedIn> getLoginStatus(SharedPreferences sharedPreferences) {
        fetchSharedPreference(sharedPreferences);
        return userLoggedIn;
    }

    private void fetchSharedPreference(SharedPreferences sharedPreferences){
        LoggedIn loggedIn = new LoggedIn(sharedPreferences.getBoolean(Constants.LoginInInfo.LOGGED_IN,false),
                sharedPreferences.getInt(Constants.LoginInInfo.TYPE,-1),sharedPreferences.getString(Constants.LoginInInfo.FIREBASE_UID,null));
        if(loggedIn.isLogin()){
            this.userLoggedIn.setValue(loggedIn);
        }
        else {
            this.userLoggedIn.setValue(null);
        }
    }
}
