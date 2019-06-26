package com.example.policyfolio.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Repo.Database.DataClasses.Documents;
import com.example.policyfolio.Repo.Repository;

public class DocumentViewModel extends ViewModel {
    private Repository repository;
    private String uId;
    private LiveData<Documents> documents;

    public void initiateRepo(Context context){
        repository = Repository.getInstance(context);
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public LiveData<Documents> fetchDocument() {
        if(documents == null)
            documents = repository.fetchDocument(uId);
        return documents;
    }
}