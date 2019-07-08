package com.example.policyfolio.viewmodels.base;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.util.Constants;
import com.example.policyfolio.data.firebase.classes.LogInData;
import com.example.policyfolio.data.Repository;

public class BasicLoginViewModel extends ViewModel {

    private MutableLiveData<LogInData> userLoggedIn = new MutableLiveData<>();
    private static Repository repository;

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

    public Repository getRepository() {
        return repository;
    }
}
