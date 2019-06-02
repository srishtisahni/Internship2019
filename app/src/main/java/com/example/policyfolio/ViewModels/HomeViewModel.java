package com.example.policyfolio.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.policyfolio.Repo.Repository;
import com.google.firebase.auth.FirebaseUser;

public class HomeViewModel extends ViewModel {

    private int type;
    private String firebaseToken;
    private Repository repository = Repository.getINSTANCE();

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }


}
