package com.example.policyfolio.UI.CallBackListeners;

import com.example.policyfolio.DataClasses.Facebook;

public interface LoginCallback {
    void FacebookSignUp(Facebook facebook);
    void enterEmail();
    void enterPhone();
    void GoogleSignUp();
    void Login();
    void forgotPassword();
    void EmailNext();
    void PhoneSignUp();
    void SignUpEmailAndPassword();
}
