package com.example.policyfolio.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.LoggedIn;

public class WelcomeViewModel extends ViewModel {

    private MutableLiveData<LoggedIn> userLoggedIn = new MutableLiveData<>();

    public MutableLiveData<LoggedIn> getLoginStatus(SharedPreferences sharedPreferences) {
        fetchSharedPreference(sharedPreferences);
        return userLoggedIn;
    }

    private void fetchSharedPreference(SharedPreferences sharedPreferences){
        LoggedIn loggedIn = new LoggedIn(sharedPreferences.getBoolean(Constants.Login.LOGGED_IN,false),
                sharedPreferences.getInt(Constants.Login.TYPE,-1),sharedPreferences.getString(Constants.Login.FIREBASE_TOKEN,null));
        if(loggedIn.isLogin()){
            this.userLoggedIn.setValue(loggedIn);
        }
        else {
            this.userLoggedIn.setValue(null);
        }
    }
}
