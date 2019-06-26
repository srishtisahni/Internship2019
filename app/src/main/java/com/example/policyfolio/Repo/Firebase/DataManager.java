package com.example.policyfolio.Repo.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.policyfolio.Repo.Database.DataClasses.Documents;
import com.example.policyfolio.Repo.Firebase.DataClasses.Query;
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
import com.google.firebase.firestore.DocumentReference;
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
                        if(user.getEmail()!=null){
                            firebaseFirestore.collectionGroup(Constants.FirebaseDataManager.COLLECTION_NOMINEE)
                                    .whereEqualTo(Constants.Nominee.EMAIL,user.getEmail())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                                                final ArrayList<Nominee> nominees = new ArrayList<>();
                                                for (int i = 0; i < documentSnapshots.size(); i++) {
                                                    nominees.add(documentSnapshots.get(i).toObject(Nominee.class));
                                                    nominees.get(i).setPfId(user.getId());
                                                    firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                                                            .document(nominees.get(i).getUserId())
                                                            .collection(Constants.FirebaseDataManager.COLLECTION_NOMINEE)
                                                            .document(nominees.get(i).getEmail())
                                                            .set(nominees.get(i));
                                                }
                                            }
                                            else {
                                                Log.e("ERROR", task.getException().getMessage());
                                            }
                                        }
                                    });
                        }
                        update.setValue(true);                                                      //Returns true if the user is added to firestore, false otherwise
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        update.setValue(false);
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
                                    if(user!=null) {
                                        user.setLastUpdated(System.currentTimeMillis()/1000);
                                        appDatabase.policyFolioDao().putUser(user);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    public void fetchPolicies(String id, final AppDatabase appDatabase) {
        ////Updates the local database using the fetched policy information from firestore
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                .document(id)
                .collection(Constants.FirebaseDataManager.COLLECTION_POLICIES)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            final ArrayList<Policy> policies = new ArrayList<>();
                            for (int i = 0; i < documentSnapshots.size(); i++) {
                                policies.add(documentSnapshots.get(i).toObject(Policy.class));
                                policies.get(i).setLastUpdated(System.currentTimeMillis()/1000);
                            }
                            appExecutors.diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    appDatabase.policyFolioDao().putPolicies(policies);
                                }
                            });
                        }
                    }
                });
    }

    public LiveData<Integer> checkIfUserExistsEmail(String email, final AppDatabase appDatabase) {
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
                                result.setValue(-1);                  //Returns the type current login/signUp type if it doesn't
                            }
                        }
                        else {
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
                                result.setValue(-1);      //Returns the type "PHONE" if it doesn't
                            }
                        }
                        else {
                            result.setValue(null);                          //Returns null in case of an exception
                        }
                    }
                });
        return result;
    }

    public void fetchProviders(int type, final AppDatabase appDatabase) {
        //Updates the local database using the fetched provider information from firestore
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_PROVIDERS)
                .whereEqualTo(Constants.InsuranceProviders.TYPE,type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            final ArrayList<InsuranceProvider> insuranceProviders = new ArrayList<>();
                            for (int i = 0; i < documentSnapshots.size(); i++) {
                                insuranceProviders.add(documentSnapshots.get(i).toObject(InsuranceProvider.class));
                                insuranceProviders.get(i).setLastUpdated(System.currentTimeMillis()/1000);
                            }
                            appExecutors.diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    appDatabase.policyFolioDao().putProviders(insuranceProviders);
                                }
                            });
                        }
                    }
                });
    }

    public void fetchProviders(final AppDatabase appDatabase) {
        //Updates the local database using the fetched provider information from firestore
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_PROVIDERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            final ArrayList<InsuranceProvider> insuranceProviders = new ArrayList<>();
                            for (int i = 0; i < documentSnapshots.size(); i++) {
                                insuranceProviders.add(documentSnapshots.get(i).toObject(InsuranceProvider.class));
                                insuranceProviders.get(i).setLastUpdated(System.currentTimeMillis()/1000);
                            }
                            appExecutors.diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    appDatabase.policyFolioDao().putProviders(insuranceProviders);
                                }
                            });
                        }
                    }
                });
    }

    public void fetchNominees(String uId, final AppDatabase appDatabase) {
        //Updates the local database using the fetched nominee information from firestore
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                .document(uId)
                .collection(Constants.FirebaseDataManager.COLLECTION_NOMINEE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            final ArrayList<Nominee> nominees = new ArrayList<>();
                            for (int i = 0; i < documentSnapshots.size(); i++) {
                                nominees.add(documentSnapshots.get(i).toObject(Nominee.class));
                                nominees.get(i).setLastUpdated(System.currentTimeMillis()/1000);
                            }
                            appExecutors.diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    appDatabase.policyFolioDao().putNominees(nominees);
                                }
                            });
                        }
                    }
                });
    }

    public LiveData<Boolean> addPolicy(Policy policy) {
        final MutableLiveData<Boolean> complete = new MutableLiveData<>();
        //Uploads Policy Information to the firestore database
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                .document(policy.getUserId())
                .collection(Constants.FirebaseDataManager.COLLECTION_POLICIES)
                .document(policy.getPolicyNumber())
                .set(policy)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            complete.setValue(true);                                                //Return true if Policy is added to firestore
                        else {
                            complete.setValue(false);
                        }
                    }
                });
        return complete;
    }

    public LiveData<Boolean> updatePolicies(String uid, List<Policy> policies) {
        final MutableLiveData<Boolean> updated = new MutableLiveData<>();
        updated.setValue(true);
        for(int i=0;i<policies.size();i++) {
            firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                    .document(uid)
                    .collection(Constants.FirebaseDataManager.COLLECTION_POLICIES)
                    .document(policies.get(i).getPolicyNumber())
                    .set(policies.get(i))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful() && !updated.getValue())
                                updated.setValue(true);
                            else {
                                updated.setValue(false);
                            }
                        }
                    });
        }
        return updated;
    }

    public LiveData<Boolean> addNominee(final Nominee nominee, final AppDatabase appDatabase) {
        final MutableLiveData<Boolean> result = new MutableLiveData<>();
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                .whereEqualTo(Constants.User.EMAIL,nominee.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            String userId = Constants.Nominee.DEFAULT_PFID;
                            for (int i = 0; i < documentSnapshots.size(); i++)
                               userId = documentSnapshots.get(i).toObject(User.class).getId();
                            nominee.setPfId(userId);
                            appExecutors.diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    appDatabase.policyFolioDao().putNominee(nominee);
                                }
                            });
                            firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                                    .document(nominee.getUserId())
                                    .collection(Constants.FirebaseDataManager.COLLECTION_NOMINEE)
                                    .document(nominee.getEmail())
                                    .set(nominee)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                                result.setValue(true);
                                            else
                                                result.setValue(false);
                                        }
                                    });
                        }
                        else {
                            result.setValue(false);
                        }
                    }
                });
        return result;
    }

    public LiveData<ArrayList<User>> fetchNomineeUsers(String uId) {
        final MutableLiveData<ArrayList<User>> result = new MutableLiveData<>();
        firebaseFirestore.collectionGroup(Constants.FirebaseDataManager.COLLECTION_NOMINEE)
                .whereEqualTo(Constants.Nominee.PFID,uId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            final ArrayList<User> users = new ArrayList<>();
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            for (int i = 0; i < documentSnapshots.size(); i++){
                                String userId = documentSnapshots.get(i).toObject(Nominee.class).getUserId();
                                firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                                        .document(userId)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    User user = task.getResult().toObject(User.class);
                                                    users.add(user);
                                                    result.setValue(users);
                                                }
                                            }
                                        });
                            }
                            result.setValue(users);
                        }
                    }
                });
        return result;
    }

    public void fetchPoliciesForNominee(String email, String userId, final AppDatabase appDatabase) {
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_USERS)
                .document(userId)
                .collection(Constants.FirebaseDataManager.COLLECTION_POLICIES)
                .whereEqualTo(Constants.Policy.NOMINEE,email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            final ArrayList<Policy> policies = new ArrayList<>();
                            for (int i = 0; i < documentSnapshots.size(); i++) {
                                policies.add(documentSnapshots.get(i).toObject(Policy.class));
                                policies.get(i).setLastUpdated(System.currentTimeMillis()/1000);
                            }
                            appExecutors.diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    appDatabase.policyFolioDao().putPolicies(policies);
                                }
                            });
                        }
                    }
                });
    }

    public LiveData<String> saveQuery(Query query) {
        final MutableLiveData<String> result = new MutableLiveData<>();
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_QUERIES)
                .add(query)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.getResult()!=null) {
                            String id = task.getResult().getId();
                            result.setValue(id);
                        }
                        else {
                            result.setValue(null);
                        }
                    }
                });
        return result;
    }

    public void fetchDocuments(String uId, final AppDatabase appDatabase) {
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_DOCUMENTS)
                .document(uId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            final Documents documents = task.getResult().toObject(Documents.class);
                            appExecutors.diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    appDatabase.policyFolioDao().putDocuments(documents);
                                }
                            });
                        }
                    }
                });
    }

    public void addDocuments(Documents documents) {
        firebaseFirestore.collection(Constants.FirebaseDataManager.COLLECTION_DOCUMENTS)
                .document(documents.getUserId())
                .set(documents)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Log.e("DOCUMENT VAULT","Vault Initiated");
                        else
                            Log.e("DOCUMENT VAULT", "Unable to Initiate Vault");
                    }
                });
    }
}
