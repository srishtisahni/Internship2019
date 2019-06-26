package com.example.policyfolio.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Repo.Firebase.DataClasses.Query;
import com.example.policyfolio.Repo.Repository;

public class HelpViewModel extends ViewModel {
    private Repository repository;
    private String uId;

    private int type;
    private String query;

    public void initiateRepo(Context context){
        repository = Repository.getInstance(context);
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public LiveData<String> saveQuery() {
        return repository.saveQuery(new Query(uId,query,type));
    }
}
