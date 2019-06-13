package com.example.policyfolio.Util.CallBackListeners;

import com.example.policyfolio.Repo.Facebook.DataClasses.FacebookData;

public interface LoginCallback {
    void FacebookSignUp(FacebookData facebookData);
    void enterEmail();
    void enterPhone();
    void GoogleSignUp();
    void Login();
    void forgotPassword();
    void EmailNext();
    void PhoneSignUp();
    void SignUpEmailAndPassword();
}
