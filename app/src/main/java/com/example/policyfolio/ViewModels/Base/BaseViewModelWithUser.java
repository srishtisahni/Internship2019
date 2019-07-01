package com.example.policyfolio.ViewModels.Base;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Data.Local.Classes.Notifications;
import com.example.policyfolio.Data.Local.Classes.User;
import com.example.policyfolio.Data.Repository;
import com.example.policyfolio.Util.Constants;
import com.facebook.login.LoginManager;

import java.util.List;

public class BaseViewModelWithUser extends ViewModel {
    //Instance of the Repository
    private static Repository repository;

    //Binded Live Data
    private static LiveData<User> user;

    private static String Uid;
    private static int loginType;

    public void initiateRepo(Context context) {
        if(repository==null)
            repository = Repository.getInstance(context);
    }

    public void setUid(String uId) {
        Uid = uId;
    }

    public String getUid() {
        return Uid;
    }

    public LiveData<User> getUser() {
        if(user == null)
            user = repository.fetchUser(Uid);
        return user;
    }

    public Repository getRepository() {
        return repository;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        BaseViewModelWithUser.loginType = loginType;
    }

    public LiveData<Boolean> logOut() {
        if(loginType == Constants.LoginInInfo.Type.FACEBOOK) {
            LoginManager.getInstance().logOut();
        }
        return getRepository().logOut(getUid());
    }


    public LiveData<List<Notifications>> getAllNotificatios() {
        return repository.getAllNotifications();
    }

    public void deleteAllNotifications() {
        repository.deleteAllNotifications();
    }
}
