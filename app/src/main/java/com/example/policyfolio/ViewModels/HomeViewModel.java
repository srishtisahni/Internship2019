package com.example.policyfolio.ViewModels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Repo.Database.DataClasses.InsuranceProvider;
import com.example.policyfolio.Repo.Database.DataClasses.Policy;
import com.example.policyfolio.Repo.Database.DataClasses.User;
import com.example.policyfolio.Repo.Repository;

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
//        Log.e("PROVIDERS",providers.toString());
        return providers;
    }

    public LiveData<Boolean> logOut() {
        return repository.logOut(Uid);
    }
}
