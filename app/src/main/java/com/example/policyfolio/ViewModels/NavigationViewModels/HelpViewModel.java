package com.example.policyfolio.ViewModels.NavigationViewModels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Repo.Repository;

public class HelpViewModel extends ViewModel {
    Repository repository;

    public void initiateRepo(Context context){
        repository = Repository.getInstance(context);
    }
}
