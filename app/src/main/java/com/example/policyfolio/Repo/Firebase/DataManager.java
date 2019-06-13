package com.example.policyfolio.Repo.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.policyfolio.Util.Constants;
import com.example.policyfolio.Repo.Database.DataClasses.InsuranceProvider;
import com.example.policyfolio.Repo.Database.DataClasses.Nominee;
import com.example.policyfolio.Repo.Database.DataClasses.Policy;
import com.example.policyfolio.Repo.Database.DataClasses.User;
import com.example.policyfolio.Util.DataFetch.AppExecutors;
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

public class DataManager {
    
    private  static DataManager INSTANCE;
    private FirebaseFirestore firebaseFirestore;
    private AppExecutors appExecutors;
    private DataManager(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        appExecutors = new AppExecutors();
    }

    //All **EXCEPTIONS** are printed in error log with the tag "EXCEPTION" along with the exception Message

    public static DataManager getInstance() {
        //Singleton Pattern
        if(INSTANCE == null)
            INSTANCE = new DataManager();
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    public LiveData<Boolean> addUser(final User user) {
        //Add User to Firebase Firestore
        final MutableLiveData<Boolean> update = new MutableLiveData<>();
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
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
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            final User user=task.getResult().toObject(User.class);
                            appExecutors.diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    if(user!=null)
                                        appDatabase.policyFolioDao().putUser(user);
                                }
                            });
                        }
                        else {
                            Log.e("EXCEPTION",task.getException().getMessage());
                        }
                    }
                });
    }

    public void fetchPolicies(String id, final AppDatabase appDatabase) {
        ////Updates the local database using the fetched policy information from firestore
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                .document(id)
                .collection(Constants.FirebaseDataManager.POLICIES_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            final ArrayList<Policy> policies = new ArrayList<>();
                            for (int i = 0; i < documentSnapshots.size(); i++)
                                policies.add(documentSnapshots.get(i).toObject(Policy.class));
                            Log.e("POLICIES",policies.size()+"");
                            appExecutors.diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    appDatabase.policyFolioDao().putPolicies(policies);
                                }
                            });
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
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                .whereEqualTo(Constants.User.EMAIL,email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<User> users = new ArrayList<User>(task.getResult().toObjects(User.class));
                            if(users.size()>0) {
                                final User user = users.get(0);
                                appExecutors.diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(user!=null)
                                            appDatabase.policyFolioDao().putUser(user);
                                    }
                                });
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
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                .whereEqualTo(Constants.User.PHONE,phone)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<User> users = new ArrayList<User>(task.getResult().toObjects(User.class));
                            if(users.size()>0) {
                                final User user = users.get(0);
                                appExecutors.diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(user!=null)
                                            appDatabase.policyFolioDao().putUser(user);
                                    }
                                });
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
        //Updates the local database using the fetched provider information from firestore
        firebaseFirestore.collection(Constants.FirebaseDataManager.PROVIDERS_COLLECTION)
                .whereEqualTo(Constants.InsuranceProviders.TYPE,type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            final ArrayList<InsuranceProvider> insuranceProviders = new ArrayList<>();
                            for (int i = 0; i < documentSnapshots.size(); i++)
                                insuranceProviders.add(documentSnapshots.get(i).toObject(InsuranceProvider.class));
                            appExecutors.diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    appDatabase.policyFolioDao().putProviders(insuranceProviders);
                                }
                            });
                        }
                        else {
                            Log.e("EXCEPTION", task.getException().getMessage());
                        }
                    }
                });
    }

    public void fetchNominees(String uId, final AppDatabase appDatabase) {
        //Updates the local database using the fetched nominee information from firestore
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                .document(uId)
                .collection(Constants.FirebaseDataManager.NOMINEE_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            final ArrayList<Nominee> nominees = new ArrayList<>();
                            for (int i = 0; i < documentSnapshots.size(); i++)
                                nominees.add(documentSnapshots.get(i).toObject(Nominee.class));
                            appExecutors.diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    appDatabase.policyFolioDao().putNominees(nominees);
                                }
                            });
                        }
                        else {
                            Log.e("EXCEPTION", task.getException().getMessage());
                        }
                    }
                });
    }

    public LiveData<Boolean> addPolicy(Policy policy) {
        final MutableLiveData<Boolean> complete = new MutableLiveData<>();
        //Uploads Policy Information to the firestore database
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                .document(policy.getUserId())
                .collection(Constants.FirebaseDataManager.POLICIES_COLLECTION)
                .document(policy.getPolicyNumber())
                .set(policy)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            complete.setValue(true);                                                //Return true if Policy is added to firestore
                        else {
                            complete.setValue(false);
                            Log.e("EXCEPTION",task.getException().getMessage());
                        }
                    }
                });
        return complete;
    }
}
