package com.example.policyfolio.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Data.Local.Classes.Documents;
import com.example.policyfolio.Data.Local.Classes.InsuranceProvider;
import com.example.policyfolio.Data.Local.Classes.Notifications;
import com.example.policyfolio.Data.Local.Classes.Policy;
import com.example.policyfolio.Data.Local.Classes.User;
import com.example.policyfolio.Data.Repository;
import com.example.policyfolio.Util.Constants;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    //Instance of the Repository
    private Repository repository;

    //Binded Live Data
    private LiveData<User> user;
    private LiveData<List<Policy>> policies;
    private LiveData<List<InsuranceProvider>> providers;

    //Private data information
    private int type;
    private String Uid;

    public void initiateRepo(Context context) {
        repository = Repository.getInstance(context);
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setUid(String Uid) {
        this.Uid = Uid;
    }

    public String getUid() {
        return Uid;
    }

    public LiveData<User> getUser() {
        if(user == null)
            user = repository.fetchUser(Uid);
        return user;
    }

    public LiveData<List<Policy>> getPolicies() {
        if(policies == null)
            policies = repository.fetchPolicies(Uid);
        return policies;
    }

    public LiveData<List<InsuranceProvider>> getProviders() {
        if(providers == null)
            providers = repository.fetchAllProviders();
        return providers;
    }

    public LiveData<Boolean> logOut() {
        if(type == Constants.LoginInInfo.Type.FACEBOOK) {
            LoginManager.getInstance().logOut();
        }
        return repository.logOut(Uid);
    }

    public LiveData<Boolean> updatePolicies(List<Policy> policies) {
        return repository.updatePolicies(Uid, policies);
    }

    public LiveData<List<Long>> addNotifications(Notifications notification, int type) {
        ArrayList<Notifications> notifications = new ArrayList<>();
        if(type == Constants.Policy.Premium.PREMIUM_ANNUALLY)
            notifications.add(notification);
        notifications.add(notification);
        notifications.add(notification);
        notifications.add(notification);
        return repository.addNotifications(notifications);
    }

    public LiveData<List<Notifications>> getAllNotificatios() {
        return repository.getAllNotifications();
    }

    public void deleteAllNotifications() {
        repository.deleteAllNotifications();
    }

    public LiveData<Boolean> addDocumentsVault() {
        return repository.addDocumentsVault(new Documents(Uid,null,null,null,
                null,null,null,null,null,null,
                null,null,null));
    }
}
