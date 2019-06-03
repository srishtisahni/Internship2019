package com.example.policyfolio.Repo.Firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DataManagement {
    
    private  static DataManagement INSTANCE;
    private FirebaseFirestore firebaseFirestore;
    private DataManagement(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public static DataManagement getInstance() {
        if(INSTANCE == null)
            INSTANCE = new DataManagement();
        return INSTANCE;
    }

    public LiveData<Boolean> addUser(final User user) {
        final MutableLiveData<Boolean> update = new MutableLiveData<>();
        firebaseFirestore.collection(Constants.Firestrore.COLLECTION_USERS)
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        update.setValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        update.setValue(false);
                        Log.e("EXCEPTION", e.getMessage());
                    }
                });
        return update;
    }
}
