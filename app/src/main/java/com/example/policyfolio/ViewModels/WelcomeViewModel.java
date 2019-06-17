package com.example.policyfolio.ViewModels;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Repo.Firebase.DataClasses.LogInData;
import com.example.policyfolio.Repo.Repository;

public class WelcomeViewModel extends ViewModel {

    private MutableLiveData<LogInData> userLoggedIn = new MutableLiveData<>();
    private Repository repository;

    public void initiateRepo(Context context) {
        repository = Repository.getInstance(context);
    }

    public LiveData<LogInData> getLoginStatus(SharedPreferences sharedPreferences) {
        fetchSharedPreference(sharedPreferences);
        return userLoggedIn;
    }

    private void fetchSharedPreference(SharedPreferences sharedPreferences){
        LogInData logInData = new LogInData(sharedPreferences.getBoolean(Constants.LoginInInfo.LOGGED_IN,false),
                sharedPreferences.getInt(Constants.LoginInInfo.TYPE,-1),sharedPreferences.getString(Constants.LoginInInfo.FIREBASE_UID,null));
        if(logInData.isLogin()){
            this.userLoggedIn.setValue(logInData);
        }
        else {
            this.userLoggedIn.setValue(null);
        }
    }
}
