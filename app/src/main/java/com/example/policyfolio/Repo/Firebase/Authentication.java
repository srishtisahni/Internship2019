package com.example.policyfolio.Repo.Firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executor;

public class Authentication {
    private GoogleSignInOptions gso;
    private GoogleSignInClient client;
    private FirebaseAuth mAuth;

    public Authentication() {
        mAuth = FirebaseAuth.getInstance();
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

    public LiveData<FirebaseUser> googleAuthentication(Intent data, Executor context) {
        final MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
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
            auth.setValue(null);
        }
        return auth;
    }

    public LiveData<FirebaseUser> facebookFirebaseUser(Executor context) {
        String token = AccessToken.getCurrentAccessToken().getToken();
        final MutableLiveData<FirebaseUser> auth = new MutableLiveData<>();
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
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
        return auth;
    }
}
