package com.example.policyfolio.Repo.Firebase;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Authentication {

    private static Authentication INSTANCE;
    private GoogleSignInOptions gso;
    private GoogleSignInClient client;
    private FirebaseAuth mAuth;

    private Authentication() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static Authentication getInstane(){
        if(INSTANCE == null)
            INSTANCE = new Authentication();
        return INSTANCE;
    }

    public void initiateGoogleLogin(String id, Context context) {
        if(gso == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(id)
                    .requestEmail()
                    .build();
        }
        client = GoogleSignIn.getClient(context,gso);
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return client;
    }

    public LiveData<FirebaseUser> googleAuthentication(Intent data) {
        final MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                auth.setValue(user);
                            } else {
                                auth.setValue(null);
                            }
                        }
                    });
        } catch (ApiException e) {
            e.printStackTrace();
            Log.e("EXCEPTION",e.getStatusCode() + " "+ e.getMessage());
            auth.setValue(null);
        }
        return auth;
    }

    public LiveData<FirebaseUser> facebookFirebaseUser() {
        String token = AccessToken.getCurrentAccessToken().getToken();
        final MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            auth.setValue(user);
                        } else {
                            Log.e("EXCEPTION",task.getException().getMessage());
                            auth.setValue(null);
                        }
                    }
                });
        return auth;
    }

    public LiveData<FirebaseUser> phoneSignUp(String phone, Activity activity) {
        final MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, activity , new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            auth.setValue(user);
                        }
                        else {
                            Log.e("EXCEPTION",task.getException().getMessage());
                            auth.setValue(null);
                        }
                    }
                });
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                e.printStackTrace();
                Log.e("EXCEPTION", e.getMessage());
                auth.setValue(null);
            }
        });
        return  auth;
    }

    public LiveData<FirebaseUser> SignUp(String email, String password) {
        final MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    auth.setValue(user);
                }
                else {
                    Log.e("EXCEPTION",task.getException().getMessage());
                    auth.setValue(null);
                }
            }
        });
        return auth;
    }

    public LiveData<FirebaseUser> Login(String email, String password) {
        final MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    auth.setValue(user);
                }
                else {
                    Log.e("EXCEPTION",task.getException().getMessage());
                    auth.setValue(null);
                }
            }
        });
        return auth;
    }
}
