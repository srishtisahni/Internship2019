package com.example.policyfolio.Repo.Firebase;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.Policy;
import com.example.policyfolio.DataClasses.User;
import com.example.policyfolio.Repo.Database.AppDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

    public void fetchUser(String id, final AppDatabase appDatabase) {
        firebaseFirestore.collection(Constants.FirebaseDataManagement.COLLECTION_USERS)
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User user=task.getResult().toObject(User.class);
                            new AsyncTask<User, Void, Void>() {
                                @Override
                                protected Void doInBackground(User... users) {
                                    User user = users[0];
                                    appDatabase.policyFolioDao().putUser(user);
                                    return null;
                                }
                            }.execute(user);
                        }
                        else {
                            Log.e("EXCEPTION",task.getException().getMessage());
                        }
                    }
                });
    }

    public void fetchPolicies(String id, final AppDatabase appDatabase) {
        firebaseFirestore.collection(Constants.FirebaseDataManagement.COLLECTION_USERS)
                .document(id)
                .collection(Constants.FirebaseDataManagement.POLICIES_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            ArrayList<Policy> policies = new ArrayList<>();
                            for (int i = 0; i < documentSnapshots.size(); i++)
                                policies.add(documentSnapshots.get(i).toObject(Policy.class));
                            new AsyncTask<List<Policy>, Void, Void>() {
                                @Override
                                protected Void doInBackground(List<Policy>... lists) {
                                    List<Policy> policies = lists[0];
                                    appDatabase.policyFolioDao().putPolicies(policies);
                                    return null;
                                }
                            }.execute(policies);
                        }
                        else {
                            Log.e("EXCEPTION",task.getException().getMessage());
                        }
                    }
                });
    }
}
