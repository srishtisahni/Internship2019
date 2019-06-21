package com.example.policyfolio.ViewModels.NavigationViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Repo.Database.DataClasses.Nominee;
import com.example.policyfolio.Repo.Repository;
import com.example.policyfolio.Util.Constants;

import java.util.List;

public class NomineeViewModel extends ViewModel {

    private Repository repository;
    private LiveData<List<Nominee>> nominees;

    private String uId;
    private int relation;
    private String email;
    private String name;
    private String phone;
    private String alternateNumber;

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

    public LiveData<Boolean> addNominee() {
        return repository.addNominee(new Nominee(uId,Constants.Nominee.DEFAULT_PFID,name,relation,email,phone,alternateNumber));
    }
}
