package com.example.policyfolio.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.policyfolio.data.firebase.DataManager;
import com.example.policyfolio.data.local.AppDatabase;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class FirebaseAuthManager {

    private static FirebaseAuthManager INSTANCE;
    private GoogleSignInOptions gso;
    private GoogleSignInClient client;
    private FirebaseAuth mAuth;
    private String verificationId;

    private FirebaseAuthManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseAuthManager getInstance(){
        //Singleton Pattern
        if(INSTANCE == null)
            INSTANCE = new FirebaseAuthManager();
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    //All **EXCEPTIONS** are printed in error log with the tag "EXCEPTION" along with the exception Message

    public void initiateGoogleLogin(String id, Context context) {
        //Initializing parameters for google sign in
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
        //Authenticating Google Account based in the email choice by the user
        final MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {    //Returns user if authentication is complete, null otherwise
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
        //Facebook Authentication using Firebase
        String token = AccessToken.getCurrentAccessToken().getToken();
        final MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();       //Returns user if authentication is complete, null otherwise
                            auth.setValue(user);
                        } else {
                            Log.e("EXCEPTION",task.getException().getMessage());
                            auth.setValue(null);
                        }
                    }
                });
        return auth;
    }

    public LiveData<FirebaseUser> sendOTP(String phone, Activity activity) { // Activity is passed for verification
        //Signing in  using Phone Number.
        MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, activity , new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                e.printStackTrace();
                Log.e("EXCEPTION", e.getMessage());
                auth.setValue(null);
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                FirebaseAuthManager.this.verificationId = verificationId;
                Toast.makeText(activity,"An OTP has been sent to "+phone,Toast.LENGTH_SHORT).show();
                super.onCodeSent(verificationId, forceResendingToken);
            }
        });
        return  auth;
    }

    public LiveData<FirebaseUser> phoneSignUp(String otp) {
        MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,otp);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();         //Returns user if authentication is complete, null otherwise
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

    public LiveData<FirebaseUser> SignUpEmailPassword(String email, String password) {
        //Signing Up using Email and Password
        final MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();         //Returns user if signUp is complete, null otherwise
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
        //Loging In using Email and Password
        final MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();          //Returns user if Login is complete, null otherwise
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

    public LiveData<Boolean> resetPassword(String email) {
        //Send reset password email
        final MutableLiveData<Boolean> code = new MutableLiveData<>();
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                code.setValue(true);                    //Returns true if a reset email is sent, false otherwise
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                code.setValue(false);
                Log.e("EXCEPTION",e.getMessage());
            }
        });
        return code;
    }

    public LiveData<Integer> checkIfUserExistsEmail(Intent data, AppDatabase appDatabase) {
        String email;
        try {
            //Extracting email from user's intent to verify if the account exists
            email = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class).getEmail();
            return DataManager.getInstance().checkIfUserExistsEmail(email,appDatabase);
        } catch (ApiException e) {
            Log.e("EXCEPTION", e.getMessage());
            MutableLiveData<Integer> nullValue = new MutableLiveData<>();
            nullValue.setValue(null);               //Returns null in case of an exception
            return nullValue;
        }
    }

    public LiveData<Boolean> logOut(String uid) {
        MutableLiveData<Boolean> completed = new MutableLiveData<>();
        mAuth.signOut();
        completed.setValue(true);
        return completed;
    }
}
