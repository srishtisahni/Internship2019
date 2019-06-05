package com.example.policyfolio.Repo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.policyfolio.DataClasses.Facebook;
import com.example.policyfolio.DataClasses.User;
import com.example.policyfolio.Repo.Database.AppDatabase;
import com.example.policyfolio.Repo.Facebook.GraphAPI;
import com.example.policyfolio.Repo.Firebase.Authentication;
import com.example.policyfolio.Repo.Firebase.DataManagement;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;

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
        updateUser(user);
        return dataManagement.addUser(user);
    }

    public void updateUser(User user) {
        cache.addUser(user);
        new AsyncTask<User, Void, Void>() {
            @Override
            protected Void doInBackground(User... users) {
                User user = users[0];
                appDatabase.policyFolioDao().putUser(user);
                return null;
            }
        }.execute(user);
    }

    public LiveData<User> fetchUser(final String id) {
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        if(cache.getUser(id) != null)
            userMutableLiveData.setValue(cache.getUser(id));
        else{
            new AsyncTask<String, Void, User>() {
                @Override
                protected User doInBackground(String... strings) {
                    String id = strings[0];
                    User user = appDatabase.policyFolioDao().getUser(id);
                    return user;
                }

                @Override
                protected void onPostExecute(User user) {
                    super.onPostExecute(user);
                    if(user != null)
                        userMutableLiveData.setValue(user);
                    else
                        dataManagement.fetchUser(id, userMutableLiveData);
                }
            }.execute(id);
        }
        return userMutableLiveData;
    }
}
