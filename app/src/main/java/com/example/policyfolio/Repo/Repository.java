package com.example.policyfolio.Repo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.policyfolio.DataClasses.Facebook;
import com.example.policyfolio.DataClasses.Policy;
import com.example.policyfolio.DataClasses.User;
import com.example.policyfolio.Repo.Database.AppDatabase;
import com.example.policyfolio.Repo.Facebook.GraphAPI;
import com.example.policyfolio.Repo.Firebase.Authentication;
import com.example.policyfolio.Repo.Firebase.DataManagement;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    private GraphAPI graphAPI;
    public static Repository INSTANCE;
    private Authentication authentication;
    private DataManagement dataManagement;
    private AppDatabase appDatabase;
    private Cache cache;

    private Repository(Context context){
        graphAPI = GraphAPI.getInstance();
        authentication = Authentication.getInstane();
        dataManagement = DataManagement.getInstance();
        appDatabase = AppDatabase.getInstance(context);
        cache = Cache.getInstance();
    }

    public static Repository getInstance(Context context) {
        if(INSTANCE==null){
            INSTANCE = new Repository(context);
        }
        return INSTANCE;
    }

    public LiveData<Facebook> getFacebookProfile(AccessToken accessToken) {
        return graphAPI.getFacebookProfile(accessToken);
    }

    public void initiateGoogleLogin(String id, Context context) {
        authentication.initiateGoogleLogin(id, context);
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return authentication.getGoogleSignInClient();
    }

    public LiveData<FirebaseUser> googleAuthentication(Intent data) {
        return authentication.googleAuthentication(data);
    }

    public LiveData<FirebaseUser> facebookFirebaseUser() {
        return authentication.facebookFirebaseUser();
    }

    public LiveData<FirebaseUser> phoneSignUp(String phone, Activity activity) {
        return authentication.phoneSignUp(phone,activity);
    }

    public LiveData<FirebaseUser> SignUp(String email, String password) {
        return authentication.SignUp(email,password);
    }

    public LiveData<FirebaseUser> Login(String email, String password) {
        return authentication.Login(email,password);
    }

    public LiveData<Boolean> resetPassword(String email) {
        return authentication.resetPassword(email);
    }

    public LiveData<Boolean> updateFirebaseUser(User user) {
        cache.addUser(user);
        new AsyncTask<User, Void, Void>() {
            @Override
            protected Void doInBackground(User... users) {
                User user = users[0];
                appDatabase.policyFolioDao().putUser(user);
                return null;
            }
        }.execute(user);
        return dataManagement.addUser(user);
    }


    public LiveData<User> fetchUser(final String id, final LifecycleOwner owner) {
        final MutableLiveData<User> user = new MutableLiveData<>();
        if(cache.getUser(id) != null)
            user.setValue(cache.getUser(id));
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
                                user.setValue(result);
                                cache.addUser(result);
                            }
                        }
                    });
                    dataManagement.fetchUser(id,appDatabase);
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
                LiveData<List<Policy>> policies = appDatabase.policyFolioDao().getPolicies(id);
                return policies;
            }

            @Override
            protected void onPostExecute(LiveData<List<Policy>> result) {
                super.onPostExecute(result);
                result.observe(owner, new Observer<List<Policy>>() {
                    @Override
                    public void onChanged(List<Policy> result) {
                        policies.setValue(result);
                        cache.addPolicies(result);
                    }
                });
                dataManagement.fetchPolicies(id,appDatabase);
            }
        }.execute(id);
        return policies;
    }

}
