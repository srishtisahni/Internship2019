package com.example.policyfolio.Data;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.example.policyfolio.Data.Local.Classes.InsuranceProvider;
import com.example.policyfolio.Data.Local.Classes.Nominee;
import com.example.policyfolio.Data.Local.Classes.Policy;
import com.example.policyfolio.Data.Local.Classes.User;
import com.example.policyfolio.Data.Local.Classes.Documents;

import java.util.HashMap;
import java.util.List;

public class Cache {
    private HashMap<String, LiveData<User>> user;
    private HashMap<String,  LiveData<List<Nominee>>> nominees;
    private HashMap<String, LiveData<List<Policy>>> policies;
    private HashMap<Integer, LiveData<List<InsuranceProvider>>> providers;
    private LiveData<List<InsuranceProvider>> allProviders;
    private HashMap<String, LiveData<Documents>> documents;
    private HashMap<String, Bitmap> images;


    private static Cache INSTANCE;

    public static Cache getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Cache();
        return INSTANCE;
    }

    @SuppressLint("UseSparseArrays")
    private Cache() {
        user = new HashMap<>();
        nominees = new HashMap<>();
        policies = new HashMap<>();
        providers = new HashMap<>();
        documents = new HashMap<>();
        images = new HashMap<>();
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

    public LiveData<Documents> getDocuments(String uId) {
        return this.documents.get(uId);
    }

    public void setDocuments(String uId, LiveData<Documents> documents) {
        this.documents.put(uId,documents);
    }

    public void addImage(String uId, String fileName, Bitmap bmp) {
        images.put(uId+"_"+fileName,bmp);
    }

    public Bitmap getImage(String uId, String fileName) {
        return images.get(uId+"_"+fileName);
    }

    public void clearImage(String uId, String filename) {
        images.remove(uId+"_"+filename);
    }
}
