package com.example.policyfolio.UI.LoginSignUp;

import com.example.policyfolio.Data.Facebook.DataClasses.FacebookData;
import com.example.policyfolio.UI.Base.BaseActivityFragmentCallback;

public interface LoginCallback extends BaseActivityFragmentCallback {
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
