package com.example.policyfolio.viewmodels;

import androidx.lifecycle.LiveData;

import com.example.policyfolio.data.local.classes.InsuranceProvider;
import com.example.policyfolio.data.local.classes.Nominee;
import com.example.policyfolio.data.local.classes.Policy;
import com.example.policyfolio.data.local.classes.User;
import com.example.policyfolio.util.Constants;
import com.example.policyfolio.viewmodels.base.BaseViewModelNavigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NomineeViewModel extends BaseViewModelNavigation {
    private LiveData<List<Nominee>> nominees;
    private HashMap<String, LiveData<List<Policy>>> policies = new HashMap<>();
    private LiveData<List<InsuranceProvider>> providers;

    private String uEmail;
    private int relation;
    private String email;
    private String name;
    private String phone;
    private String alternateNumber;

    public LiveData<List<Nominee>> getNominees() {
        if(nominees == null)
            nominees = getRepository().fetchNominees(getUid());
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
        return getRepository().addNominee(new Nominee(getUid(),Constants.Nominee.DEFAULT_PFID,name,relation,email,phone,alternateNumber));
    }

    public LiveData<List<Policy>> getPoliciesForNominee(String userId) {
        if(policies.get(userId) == null)
            policies.put(userId, getRepository().fetchPoliciesForNominee(uEmail,userId));
        return policies.get(userId);
    }

    public LiveData<List<InsuranceProvider>> getProviders() {
        if(providers == null)
            providers = getRepository().fetchAllProviders();
        return providers;
    }

    public LiveData<ArrayList<User>> fetchNomineeUsers() {
        return getRepository().fetchNomineeUsers(getUid());
    }
}
