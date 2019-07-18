package com.example.policyfolio.ui.login;

import com.example.policyfolio.data.facebook.dataclasses.FacebookData;
import com.example.policyfolio.ui.base.BaseActivityFragmentCallback;


public interface LoginCallback extends BaseActivityFragmentCallback {
    void FacebookSignUp(FacebookData facebookData);
    void GoogleSignUp();
    void Login();
    void forgotPassword();
    void UpdateDatabase();
    void phoneSignUp();
    void SignUpEmailAndPassword();
    void showSnackbar(String s);
    void openLoginFragment();

    void sendOTP(String toString);
}
