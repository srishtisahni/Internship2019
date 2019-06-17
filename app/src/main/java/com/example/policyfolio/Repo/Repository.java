package com.example.policyfolio.Repo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.policyfolio.Repo.Facebook.DataClasses.FacebookData;
import com.example.policyfolio.Repo.Database.DataClasses.InsuranceProvider;
import com.example.policyfolio.Repo.Database.DataClasses.Nominee;
import com.example.policyfolio.Repo.Database.DataClasses.Policy;
import com.example.policyfolio.Repo.Database.DataClasses.User;
import com.example.policyfolio.Repo.Database.AppDatabase;
import com.example.policyfolio.Repo.Facebook.GraphAPI;
import com.example.policyfolio.Repo.Firebase.AuthManager;
import com.example.policyfolio.Repo.Firebase.DataManager;
import com.example.policyfolio.Repo.Firebase.StorageManager;
import com.example.policyfolio.Repo.InternalStorage.ImageStorage;
import com.example.policyfolio.Util.DataFetch.AppExecutors;
import com.example.policyfolio.Util.DataFetch.Cache;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;

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

    public LiveData<Integer> checkIfUserExistsEmail(String email, Integer type) {
        //Type is used to identify the login Type as Facebook or Email
        return dataManager.checkIfUserExistsEmail(email,type,appDatabase);   //Checks if the  user with same the email exists
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

    public LiveData<String> saveImage(Bitmap bmp, String uId, String policyNumber) {
        imageStorage.saveImage(uId,policyNumber,bmp);                           //Saves image in the local storage
        return storageManager.saveImage(uId,policyNumber,bmp);                  //returns live data containing image URL
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
//        Log.e("PROVIDERS",cache.getAllProviders().toString());
        return cache.getAllProviders();                                    //Returns live data from Cache
    }

    public LiveData<Boolean> logOut(String uid) {
        return authentication.logOut(uid);
    }
}
