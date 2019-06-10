package com.example.policyfolio.Repo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.policyfolio.DataClasses.Facebook;
import com.example.policyfolio.DataClasses.InsuranceProvider;
import com.example.policyfolio.DataClasses.Policy;
import com.example.policyfolio.DataClasses.User;
import com.example.policyfolio.Repo.Database.AppDatabase;
import com.example.policyfolio.Repo.Facebook.GraphAPI;
import com.example.policyfolio.Repo.Firebase.Authentication;
import com.example.policyfolio.Repo.Firebase.DataManagement;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class Repository {
    //Fetches and stores data
    private GraphAPI graphAPI;
    public static Repository INSTANCE;
    private Authentication authentication;
    private DataManagement dataManagement;
    private AppDatabase appDatabase;
    private Cache cache;

    private Repository(Context context){
        graphAPI = GraphAPI.getInstance();
        authentication = Authentication.getInstance();
        dataManagement = DataManagement.getInstance();
        appDatabase = AppDatabase.getInstance(context);
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

    public LiveData<Boolean> updateFirebaseUser(User user) {
        cache.addUser(user);                                        //Add the current user to Cache
        new AsyncTask<User, Void, Void>() {                         //Update local database
            @Override
            protected Void doInBackground(User... users) {
                User user = users[0];
                appDatabase.policyFolioDao().putUser(user);
                return null;
            }
        }.execute(user);
        return dataManagement.addUser(user);                        //Update Firestore Database
    }


    public LiveData<User> fetchUser(final String id, final LifecycleOwner owner) {
        final MutableLiveData<User> user = new MutableLiveData<>();
        if(cache.getUser(id) != null)
            user.setValue(cache.getUser(id));                       //Sets the value from cache to speed up process
        else{
            new AsyncTask<String, Void, LiveData<User>>() {
                @Override
                protected LiveData<User> doInBackground(String... strings) {
                    String id = strings[0];
                    LiveData<User> user = appDatabase.policyFolioDao().getUser(id);
                    return user;
                }

                @Override
                protected void onPostExecute(LiveData<User> result) {
                    super.onPostExecute(result);
                    result.observe(owner, new Observer<User>() {
                        @Override
                        public void onChanged(User result) {
                            if(result!=null) {
                                user.setValue(result);          //Sets value from the local database
                                cache.addUser(result);          //Updates cache with the latest value
                            }
                        }
                    });
                    dataManagement.fetchUser(id,appDatabase);      //Uses firestore to update local database
                }
            }.execute(id);
        }
        return user;
    }

    public LiveData<List<Policy>> fetchPolicies(final String id, final LifecycleOwner owner) {
        final MutableLiveData<List<Policy>> policies = new MutableLiveData<>();
        new AsyncTask<String, Void, LiveData<List<Policy>>>() {
            @Override
            protected LiveData<List<Policy>> doInBackground(String... strings) {
                String id = strings[0];
                LiveData<List<Policy>> policies = appDatabase.policyFolioDao().getPolicies(id);     //Fetches Policies from the Local Database
                return policies;
            }

            @Override
            protected void onPostExecute(LiveData<List<Policy>> result) {
                super.onPostExecute(result);
                result.observe(owner, new Observer<List<Policy>>() {
                    @Override
                    public void onChanged(List<Policy> result) {
                        policies.setValue(result);                                                  //Updates the values sent to the user
                        cache.addPolicies(result);                                                  //Updates the values in cache
                    }
                });
                dataManagement.fetchPolicies(id,appDatabase);                                       //Updates the Local Database from Firestore
            }
        }.execute(id);
        return policies;
    }

    public LiveData<Integer> checkIfUserExistsEmail(Intent data) {
        return authentication.checkIfUserExistsEmail(data,appDatabase);         //Checks if the  user with same email as his/her google account exists
    }

    public LiveData<Integer> checkIfUserExistsEmail(String email, Integer type) {
        //Type is used to identify the login Type as Facebook or Email
        return dataManagement.checkIfUserExistsEmail(email,type,appDatabase);   //Checks if the  user with same the email exists
    }

    public LiveData<Integer> checkIfUserExistsPhone(String phone) {
        return dataManagement.checkIfUserExistsPhone(phone,appDatabase);        //Checks if the  user with same the phone number exists
    }

    public LiveData<List<InsuranceProvider>> fetchProviders(final int type, final LifecycleOwner owner) {
        final MutableLiveData<List<InsuranceProvider>> providers = new MutableLiveData<>();
        new AsyncTask<Integer, Void, LiveData<List<InsuranceProvider>>>() {
            @Override
            protected LiveData<List<InsuranceProvider>> doInBackground(Integer... integers) {
                Integer type = integers[0];
                LiveData<List<InsuranceProvider>> providers = appDatabase.policyFolioDao().getProvidersFromType(type);      //Fetch Insurance Providers from the Local Database
                return providers;
            }

            @Override
            protected void onPostExecute(LiveData<List<InsuranceProvider>> result) {
                super.onPostExecute(result);
                result.observe(owner, new Observer<List<InsuranceProvider>>() {
                    @Override
                    public void onChanged(List<InsuranceProvider> insuranceProviders) {
                        providers.setValue(insuranceProviders);                                                             //Update the values sent to user
                    }
                });
                dataManagement.fetchProviders(type,appDatabase);                                                            //Update Local Database from Firestore
            }
        }.execute(type);
        return providers;
    }
}
