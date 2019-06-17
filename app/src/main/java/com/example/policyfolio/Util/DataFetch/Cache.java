package com.example.policyfolio.Util.DataFetch;

import androidx.lifecycle.LiveData;

import com.example.policyfolio.Repo.Database.DataClasses.InsuranceProvider;
import com.example.policyfolio.Repo.Database.DataClasses.Nominee;
import com.example.policyfolio.Repo.Database.DataClasses.Policy;
import com.example.policyfolio.Repo.Database.DataClasses.User;

import java.util.HashMap;
import java.util.List;

public class Cache {
    private HashMap<String, LiveData<User>> user;
    private HashMap<String,  LiveData<List<Nominee>>> nominees;
    private HashMap<String, LiveData<List<Policy>>> policies;
    private HashMap<Integer, LiveData<List<InsuranceProvider>>> providers;
    private LiveData<List<InsuranceProvider>> allProviders;


    private static Cache INSTANCE;

    public static Cache getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Cache();
        return INSTANCE;
    }

    private Cache() {
        user = new HashMap<>();
        nominees = new HashMap<>();
        policies = new HashMap<>();
        providers = new HashMap<>();
    }

    public static void clearCache(){
        INSTANCE = new Cache();
    }

    public LiveData<List<InsuranceProvider>> getAllProviders() {
        return allProviders;
    }

    public void setAllProviders(LiveData<List<InsuranceProvider>> allProviders) {
        this.allProviders = allProviders;
    }

    public LiveData<User> getUser(String uId) {
        return user.get(uId);
    }

    public void setUser(String uId, LiveData<User> user) {
        this.user.put(uId, user);
    }

    public LiveData<List<Nominee>> getNominees(String uId) {
        return nominees.get(uId);
    }

    public void setNominees(String uId, LiveData<List<Nominee>> nominees) {
        this.nominees.put(uId,nominees);
    }

    public LiveData<List<InsuranceProvider>> getProviders(Integer type) {
        return providers.get(type);
    }

    public void setProviders(Integer type, LiveData<List<InsuranceProvider>> providers) {
        this.providers.put(type,providers);
    }

    public LiveData<List<Policy>> getPolicies(String uId) {
        return policies.get(uId);
    }

    public void setPolicies(String uId, LiveData<List<Policy>> policies) {
        this.policies.put(uId,policies);
    }
}
