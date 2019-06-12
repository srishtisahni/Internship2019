package com.example.policyfolio.Repo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.policyfolio.DataClasses.Facebook;
import com.example.policyfolio.DataClasses.InsuranceProvider;
import com.example.policyfolio.DataClasses.Nominee;
import com.example.policyfolio.DataClasses.Policy;
import com.example.policyfolio.DataClasses.User;
import com.example.policyfolio.Repo.Database.AppDatabase;
import com.example.policyfolio.Repo.Facebook.GraphAPI;
import com.example.policyfolio.Repo.Firebase.Authentication;
import com.example.policyfolio.Repo.Firebase.DataManager;
import com.example.policyfolio.Repo.Firebase.StorageManager;
import com.example.policyfolio.Repo.InternalStorage.ImageStorage;
import com.example.policyfolio.Util.AppExecutors;
import com.example.policyfolio.Util.Cache;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class Repository {
    //Fetches and stores data
    private GraphAPI graphAPI;
    public static Repository INSTANCE;
    private Authentication authentication;
    private DataManager dataManager;
    private StorageManager storageManager;
    private AppDatabase appDatabase;
    private Cache cache;
    private ImageStorage imageStorage;
    private AppExecutors appExecutors;

    private Repository(Context context){
        graphAPI = GraphAPI.getInstance();
        authentication = Authentication.getInstance();
        dataManager = DataManager.getInstance();
        storageManager = StorageManager.getInstance();
        appDatabase = AppDatabase.getInstance(context);
        cache = Cache.getInstance();
        imageStorage = ImageStorage.getInstance(context);
        appExecutors = new AppExecutors();
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

    public LiveData<Facebook> getFacebookProfile(AccessToken accessToken) {
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
        cache.addUser(user);//Add the current user to Cache
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.policyFolioDao().putUser(user);
            }
        });
        return dataManager.addUser(user);                        //Update Firestore Database
    }


    public LiveData<User> fetchUser(final String id, final LifecycleOwner owner) {
        dataManager.fetchUser(id,appDatabase);
        return appDatabase.policyFolioDao().getUser(id);
//        final MutableLiveData<User> user = new MutableLiveData<>();
//        if(cache.getUser(id) != null)
//            user.setValue(cache.getUser(id));                       //Sets the value from cache to speed up process
//        else{
//            appExecutors.mainThread().execute(new Runnable() {
//                @Override
//                public void run() {
//                    LiveData<User> result = appDatabase.policyFolioDao().getUser(id);         //Fetch User from Local Database
//                    dataManager.fetchUser(id,appDatabase);
//                    result.observe(owner, new Observer<User>() {
//                        @Override
//                        public void onChanged(User result) {
//                            if(result!=null) {
//                                user.setValue(result);          //Sets value from the local database
//                                cache.addUser(result);          //Updates cache with the latest value
//                            }
//                        }
//                    });
//                }
//            });
//        }
//        return user;
    }

    public LiveData<List<Policy>> fetchPolicies(final String id, final LifecycleOwner owner) {
        final MutableLiveData<List<Policy>> policies = new MutableLiveData<>();
        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                LiveData<List<Policy>> result = appDatabase.policyFolioDao().getPolicies(id);     //Fetches Policies from the Local Database
                dataManager.fetchPolicies(id,appDatabase);
                result.observe(owner, new Observer<List<Policy>>() {
                    @Override
                    public void onChanged(List<Policy> result) {
                        policies.setValue(result);                                                  //Updates the values sent to the user
                        cache.addPolicies(result);                                                  //Updates the values in cache
                    }
                });
            }
        });
        return policies;
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

    public LiveData<List<InsuranceProvider>> fetchProviders(final int type, final LifecycleOwner owner) {
        final MutableLiveData<List<InsuranceProvider>> providers = new MutableLiveData<>();
        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                LiveData<List<InsuranceProvider>> result = appDatabase.policyFolioDao().getProvidersFromType(type);      //Fetch Insurance Providers from the Local Database
                dataManager.fetchProviders(type,appDatabase);
                result.observe(owner, new Observer<List<InsuranceProvider>>() {
                    @Override
                    public void onChanged(List<InsuranceProvider> insuranceProviders) {
                        providers.setValue(insuranceProviders);                                                             //Update the values sent to user
                    }
                });
            }
        });
        return providers;
    }

    public LiveData<List<Nominee>> fetchNominees(final String uId, final LifecycleOwner owner) {
        dataManager.fetchNominees(uId,appDatabase);
        return appDatabase.policyFolioDao().getNomineesForUser(uId);
//        final MutableLiveData<List<Nominee>> nominees = new MutableLiveData<>();
//        appExecutors.mainThread().execute(new Runnable() {
//            @Override
//            public void run() {
//                LiveData<List<Nominee>> result =    appDatabase.policyFolioDao().getNomineesForUser(uId)            //Fetch Nominees from the Local Database
//                dataManager.fetchNominees(uId,appDatabase);
//                result.observe(owner, new Observer<List<Nominee>>() {
//                    @Override
//                    public void onChanged(List<Nominee> result) {
//                        nominees.setValue(result);                                                                  //Update the values returned to the user
//                    }
//                });
//            }
//        });
//        return nominees;
    }

    public LiveData<String> saveImage(Bitmap bmp, String uId, String policyNumber) {
        cache.saveImage(uId,policyNumber,bmp);
        imageStorage.saveImage(uId,policyNumber,bmp);
        return storageManager.saveImage(uId,policyNumber,bmp);
    }

    public LiveData<Boolean> addPolicy(final Policy policy) {
        cache.addPolicy(policy);
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.policyFolioDao().putPolicy(policy);
            }
        });
        return dataManager.addPolicy(policy);
    }

}
