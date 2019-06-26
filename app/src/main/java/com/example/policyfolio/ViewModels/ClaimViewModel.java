package com.example.policyfolio.ViewModels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Repo.Repository;

public class ClaimViewModel extends ViewModel {
    private Repository repository;
    private String uId;

    public void initiateRepo(Context context){
        repository = Repository.getInstance(context);
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
