package com.example.policyfolio.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Data.Local.Classes.InsuranceProvider;
import com.example.policyfolio.Data.Local.Classes.Nominee;
import com.example.policyfolio.Data.Local.Classes.Notifications;
import com.example.policyfolio.Data.Local.Classes.Policy;
import com.example.policyfolio.Data.Local.Classes.User;
import com.example.policyfolio.Data.Repository;
import com.example.policyfolio.Util.Constants;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NomineeViewModel extends ViewModel {

    private Repository repository;
    private LiveData<List<Nominee>> nominees;
    private HashMap<String, LiveData<List<Policy>>> policies = new HashMap<>();
    private LiveData<List<InsuranceProvider>> providers;

    private String uId;
    private String uEmail;
    private int relation;
    private String email;
    private String name;
    private String phone;
    private String alternateNumber;
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

    public LiveData<List<Nominee>> getNominees() {
        if(nominees == null)
            nominees = repository.fetchNominees(uId);
        return nominees;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlternateNumber() {
        return alternateNumber;
    }

    public void setAlternateNumber(String alternateNumber) {
        this.alternateNumber = alternateNumber;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public LiveData<Boolean> addNominee() {
        return repository.addNominee(new Nominee(uId,Constants.Nominee.DEFAULT_PFID,name,relation,email,phone,alternateNumber));
    }

    public LiveData<List<Policy>> getPoliciesForNominee(String userId) {
        if(policies.get(userId) == null)
            policies.put(userId,repository.fetchPoliciesForNominee(uEmail,userId));
        return policies.get(userId);
    }

    public LiveData<List<InsuranceProvider>> getProviders() {
        if(providers == null)
            providers = repository.fetchAllProviders();
        return providers;
    }

    public LiveData<ArrayList<User>> fetchNomineeUsers() {
        return repository.fetchNomineeUsers(uId);
    }

    public LiveData<User> fetchUser() {
        return repository.fetchUser(uId);
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
        if (loginType == Constants.LoginInInfo.Type.FACEBOOK) {
            LoginManager.getInstance().logOut();
        }
        return repository.logOut(uId);
    }
}
