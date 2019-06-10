package com.example.policyfolio.Repo.Firebase;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.policyfolio.Constants;
import com.example.policyfolio.DataClasses.InsuranceProvider;
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

    //All **EXCEPTIONS** are printed in error log with the tag "EXCEPTION" along with the exception Message

    public static DataManagement getInstance() {
        //Singleton Pattern
        if(INSTANCE == null)
            INSTANCE = new DataManagement();
        return INSTANCE;
    }

    public LiveData<Boolean> addUser(final User user) {
        //Add User to Firebase Firestore
        final MutableLiveData<Boolean> update = new MutableLiveData<>();
        firebaseFirestore.collection(Constants.FirebaseDataManagement.COLLECTION_USERS)
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        update.setValue(true);              //Returns true if the user is added to firestore, false otherwise
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
        //Updates the local database using the fetched user information from firestore
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
                                    if(user!=null)
                                        appDatabase.policyFolioDao().putUser(user);         //The Local database is updated on the background thread
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
        ////Updates the local database using the fetched policy information from firestore
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
                                    appDatabase.policyFolioDao().putPolicies(policies);         //The Local database is updated on the background thread
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

    public LiveData<Integer> checkIfUserExistsEmail(String email, final Integer type, final AppDatabase appDatabase) {
        //Checks if an account exists if the same email id
        final MutableLiveData<Integer> result = new MutableLiveData<>();
        firebaseFirestore.collection(Constants.FirebaseDataManagement.COLLECTION_USERS)
                .whereEqualTo(Constants.User.EMAIL,email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<User> users = new ArrayList<User>(task.getResult().toObjects(User.class));
                            if(users.size()>0) {
                                User user = users.get(0);
                                new AsyncTask<User, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(User... users) {
                                        User user = users[0];
                                        if(user!=null)
                                            appDatabase.policyFolioDao().putUser(user);         //The Local database is updated on the background thread
                                        return null;
                                    }
                                }.execute(user);
                                result.setValue(user.getType());        //Returns the type of the account if it exists
                            }
                            else {
                                result.setValue(type);                  //Returns the type current login/signUp type if it doesn't
                            }
                        }
                        else {
                            Log.e("EXCEPTION",task.getException().getMessage());
                            result.setValue(null);                      //Returns null in case of an exception
                        }
                    }
                });
        return result;
    }

    public LiveData<Integer> checkIfUserExistsPhone(String phone, final AppDatabase appDatabase) {
        //Checks if an account exists if the same phone number
        final MutableLiveData<Integer> result = new MutableLiveData<>();
        firebaseFirestore.collection(Constants.FirebaseDataManagement.COLLECTION_USERS)
                .whereEqualTo(Constants.User.PHONE,phone)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<User> users = new ArrayList<User>(task.getResult().toObjects(User.class));
                            if(users.size()>0) {
                                User user = users.get(0);
                                new AsyncTask<User, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(User... users) {
                                        User user = users[0];
                                        if(user!=null)
                                            appDatabase.policyFolioDao().putUser(user);         //The Local database is updated on the background thread
                                        return null;
                                    }
                                }.execute(user);
                                result.setValue(user.getType());            //Returns the type of the account if it exists
                            }
                            else {
                                result.setValue(Constants.LoginInInfo.Type.PHONE);      //Returns the type "PHONE" if it doesn't
                            }
                        }
                        else {
                            Log.e("EXCEPTION",task.getException().getMessage());
                            result.setValue(null);                          //Returns null in case of an exception
                        }
                    }
                });
        return result;
    }

    public void fetchProviders(int type, final AppDatabase appDatabase) {
        ////Updates the local database using the fetched provider information from firestore
        firebaseFirestore.collection(Constants.FirebaseDataManagement.PROVIDERS_COLLECTION)
                .whereEqualTo(Constants.InsuranceProviders.TYPE,type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            ArrayList<InsuranceProvider> insuranceProviders = new ArrayList<>();
                            for (int i = 0; i < documentSnapshots.size(); i++)
                                insuranceProviders.add(documentSnapshots.get(i).toObject(InsuranceProvider.class));
                            new AsyncTask<List<InsuranceProvider>, Void, Void>() {
                                @Override
                                protected Void doInBackground(List<InsuranceProvider>... lists) {
                                    List<InsuranceProvider> providers = lists[0];
                                    appDatabase.policyFolioDao().putProviders(providers);
                                    return null;
                                }
                            }.execute(insuranceProviders);
                        }
                        else {
                            Log.e("EXCEPTION", task.getException().getMessage());
                        }
                    }
                });
    }
}
