package com.example.policyfolio.Repo.Firebase;

import android.arch.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseUser;

public class Firestore {
    
    private  static Firestore INSTANCE;
    private FirebaseFirestore
    private Firestore(){
        
    }

    public static Firestore getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Firestore();
        return INSTANCE;
    }

    public LiveData<Boolean> addUser(FirebaseUser firebaseUser) {

    }
}
