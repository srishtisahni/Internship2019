package com.example.policyfolio.Repo.Firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
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
        firebaseFirestore.collection(Constants.FirebaseDataManagement.COLLECTION_USERS)
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

    public LiveData<User> fetchUser(String id) {
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        firebaseFirestore.collection(Constants.FirebaseDataManagement.COLLECTION_USERS)
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User user=task.getResult().toObject(User.class);
                            userMutableLiveData.setValue(user);
                        }
                        else {
                            userMutableLiveData.setValue(null);
                            Log.e("EXCEPTION",task.getException().getMessage());
                        }
                    }
                });
        return userMutableLiveData;
    }
}
