package com.example.policyfolio.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Data.Local.Classes.Notifications;
import com.example.policyfolio.Data.Repository;
import com.example.policyfolio.Util.Constants;
import com.facebook.login.LoginManager;

import java.util.List;

public class ClaimViewModel extends ViewModel {
    private Repository repository;
    private String uId;
    private int loginType;

    public void initiateRepo(Context context){
        repository = Repository.getInstance(context);
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }


    public LiveData<List<Notifications>> getAllNotificatios() {
        return repository.getAllNotifications();
    }

    public void deleteAllNotifications() {
        repository.deleteAllNotifications();
    }

    public LiveData<Boolean> logOut() {
        if(loginType == Constants.LoginInInfo.Type.FACEBOOK) {
            LoginManager.getInstance().logOut();
        }
        return repository.logOut(uId);
    }
}
