package com.example.policyfolio.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.policyfolio.data.local.classes.Notifications;
import com.example.policyfolio.data.facebook.dataclasses.FacebookData;
import com.example.policyfolio.data.local.classes.InsuranceProvider;
import com.example.policyfolio.data.local.classes.Nominee;
import com.example.policyfolio.data.local.classes.Policy;
import com.example.policyfolio.data.local.classes.User;
import com.example.policyfolio.data.local.AppDatabase;
import com.example.policyfolio.data.facebook.GraphAPI;
import com.example.policyfolio.data.firebase.AuthManager;
import com.example.policyfolio.data.local.classes.Documents;
import com.example.policyfolio.data.firebase.classes.Query;
import com.example.policyfolio.data.firebase.DataManager;
import com.example.policyfolio.data.firebase.StorageManager;
import com.example.policyfolio.data.internalstorage.ImageStorage;
import com.example.policyfolio.util.executors.AppExecutors;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Repository {
    //Fetches and stores data
    private GraphAPI graphAPI;
    public static Repository INSTANCE;
    private AuthManager authentication;
    private DataManager dataManager;
    private StorageManager storageManager;
    private AppDatabase appDatabase;
    private ImageStorage imageStorage;
    private AppExecutors appExecutors;
    private Cache cache;

    private Repository(Context context){
        graphAPI = GraphAPI.getInstance();
        authentication = AuthManager.getInstance();
        dataManager = DataManager.getInstance();
        storageManager = StorageManager.getInstance();
        appDatabase = AppDatabase.getInstance(context);
        imageStorage = ImageStorage.getInstance(context);
        appExecutors = new AppExecutors();
        cache = Cache.getInstance();
    }

    //All **UPDATES** on the **LOCAL DATABASE** occur on the background thread

    public static Repository getInstance(Context context) {
        //Singleton Pattern
        if(INSTANCE==null){
            INSTANCE = new Repository(context);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    public LiveData<FacebookData> getFacebookProfile(AccessToken accessToken) {
        return graphAPI.getFacebookProfile(accessToken);    //Fetch Facebook info
    }

    public void initiateGoogleLogin(String id, Context context) {
        authentication.initiateGoogleLogin(id, context);    //Initialize Google Sign in variables
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return authentication.getGoogleSignInClient();      //Fetch Google SignIn Client for intent initialization
    }

    public LiveData<FirebaseUser> googleAuthentication(Intent data) {
        return authentication.googleAuthentication(data);   //Perform Google Authentication
    }

    public LiveData<FirebaseUser> facebookFirebaseUser() {
        return authentication.facebookFirebaseUser();       //Perform Facebook Authentication
    }

    public LiveData<FirebaseUser> phoneSignUp(String phone, Activity activity) {
        //Activity is passed as a parameter for verification functions
        return authentication.phoneSignUp(phone,activity);  //SignUp using phone number
    }

    public LiveData<FirebaseUser> SignUp(String email, String password) {
        return authentication.SignUpEmailPassword(email,password);  //Sign Up using Email and Password
    }

    public LiveData<FirebaseUser> Login(String email, String password) {
        return authentication.Login(email,password);                //Login Using Email and Password
    }

    public LiveData<Boolean> resetPassword(String email) {
        return authentication.resetPassword(email);                 //Send Password Reset Email
    }

    public LiveData<Boolean> updateFirebaseUser(final User user) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.policyFolioDao().putUser(user);
            }
        });
        return dataManager.addUser(user);                        //Update Firestore Database
    }


    public LiveData<User> fetchUser(final String id) {
        if(cache.getUser(id) == null)
            cache.setUser(id, appDatabase.policyFolioDao().getUser(id));    //Updates Cache
        dataManager.fetchUser(id,appDatabase);                      //Updates changes from firebase
        return cache.getUser(id);                                   //Returns Live data from cache
    }

    public LiveData<List<Policy>> fetchPolicies(final String id) {
        if(cache.getPolicies(id) == null)
            cache.setPolicies(id,appDatabase.policyFolioDao().getPolicies(id));     //Updates Cache
        dataManager.fetchPolicies(id,appDatabase);                  //Updates changes from firebase
        return cache.getPolicies(id);                               //Returns Live Data from Cache
    }

    public LiveData<Integer> checkIfUserExistsEmail(Intent data) {
        return authentication.checkIfUserExistsEmail(data,appDatabase);         //Checks if the  user with same email as his/her google account exists
    }

    public LiveData<Integer> checkIfUserExistsEmail(String email) {
        return dataManager.checkIfUserExistsEmail(email,appDatabase);   //Checks if the  user with same the email exists
    }

    public LiveData<Integer> checkIfUserExistsPhone(String phone) {
        return dataManager.checkIfUserExistsPhone(phone,appDatabase);        //Checks if the  user with same the phone number exists
    }

    public LiveData<List<InsuranceProvider>> fetchProviders(final int type) {
        if(cache.getProviders(type) == null)
            cache.setProviders(type, appDatabase.policyFolioDao().getProvidersFromType(type));  //Updates Cache
        dataManager.fetchProviders(type,appDatabase);                           //Updates the local database from firebase
        return cache.getProviders(type);                                        //Returns live data from Cache
    }

    public LiveData<List<Nominee>> fetchNominees(final String uId) {
        if(cache.getNominees(uId) == null)
            cache.setNominees(uId,appDatabase.policyFolioDao().getNomineesForUser(uId)); //Updates Cache
        dataManager.fetchNominees(uId,appDatabase);                             //Updates the local database from firebase
        return cache.getNominees(uId);                                          //Returns live data from Cache
    }

    public LiveData<String> saveImage(Bitmap bmp, String uId, String fileName) {
        imageStorage.save(uId,fileName,bmp);                           //Saves image in the local storage
        cache.addImage(uId,fileName,bmp);
        return storageManager.saveImage(uId,fileName,bmp);                  //returns live data containing image URL
    }

    public LiveData<Bitmap> fetchImage(String uId, String filename){
        MutableLiveData<Bitmap> result = new MutableLiveData<>();
        if(cache.getImage(uId,filename) == null)
            cache.addImage(uId,filename,imageStorage.fetch(uId,filename));
        result.setValue(cache.getImage(uId,filename));
        storageManager.fetchImage(uId,filename,result);
        return result;
    }

    public LiveData<Boolean> deleteImage(String uId, String filename) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        cache.clearImage(uId,filename);
        if(imageStorage.delete(uId,filename)){
            storageManager.deleteImage(uId,filename,result);
        }
        else {
            result.setValue(false);
        }
        return result;
    }

    public LiveData<Boolean> addPolicy(final Policy policy) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.policyFolioDao().putPolicy(policy);                 //Adds policy to the local database
            }
        });
        return dataManager.addPolicy(policy);                                   //Updates policy on firebase
    }

    public LiveData<List<InsuranceProvider>> fetchAllProviders() {
        if(cache.getAllProviders() == null)
            cache.setAllProviders(appDatabase.policyFolioDao().getProviders());     //Updates cache
        dataManager.fetchProviders(appDatabase);                           //Updates the local database from firebase
        return cache.getAllProviders();                                    //Returns live data from Cache
    }

    public LiveData<Boolean> logOut(String uid) {
        return authentication.logOut(uid);
    }

    public LiveData<Boolean> updatePolicies(String uid, final List<Policy> policies) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.policyFolioDao().putPolicies(policies);
            }
        });
        return dataManager.updatePolicies(uid,policies);
    }

    public LiveData<List<Long>> addNotifications(final ArrayList<Notifications> notifications) {
        final MutableLiveData<List<Long>> mutableLiveData = new MutableLiveData<>();
        new AsyncTask<ArrayList<Notifications>, Void, List<Long>>() {
            @Override
            protected List<Long> doInBackground(ArrayList<Notifications>... arrayLists) {
                ArrayList<Notifications> notifications = arrayLists[0];
                return appDatabase.policyFolioDao().putNotifications(notifications);
            }

            @Override
            protected void onPostExecute(List<Long> longs) {
                super.onPostExecute(longs);
                mutableLiveData.setValue(longs);
            }
        }.execute(notifications);
        return mutableLiveData;
    }

    public LiveData<List<Notifications>> getAllNotifications() {
        return appDatabase.policyFolioDao().getAllNotifications();
    }

    public void deleteAllNotifications() {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.policyFolioDao().deleteAllNotifications();
            }
        });
    }

    public void deleteNotifications(final long id) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.policyFolioDao().deleteNotifications(id);
            }
        });
    }

    public LiveData<Boolean> addNominee(Nominee nominee) {
        return dataManager.addNominee(nominee,appDatabase);
    }

    public LiveData<List<Policy>> fetchPoliciesForNominee(String email, String userId) {
        dataManager.fetchPoliciesForNominee(email,userId,appDatabase);
        return appDatabase.policyFolioDao().getPoliciesForNominee(userId,email);
    }

    public LiveData<ArrayList<User>> fetchNomineeUsers(String uId) {
        return dataManager.fetchNomineeUsers(uId);
    }

    public LiveData<String> saveQuery(Query query) {
        return dataManager.saveQuery(query);
    }

    public LiveData<Documents> fetchDocument(String uId) {
        dataManager.fetchDocuments(uId,appDatabase);
        if(cache.getDocuments(uId) == null)
            cache.setDocuments(uId,appDatabase.policyFolioDao().getDocuments(uId));
        return cache.getDocuments(uId);
    }

    public LiveData<Boolean> addDocumentsVault(final Documents documents) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.policyFolioDao().putDocuments(documents);
            }
        });
        return dataManager.addDocuments(documents);
    }
}
