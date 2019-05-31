package com.example.policyfolio.UI.CallBackListeners;

import com.example.policyfolio.Data.Facebook;

public interface LoginFragmentCallback {
    void FacebookSignUp(Facebook facebook);
    void enterEmail();
    void GoogleSignUp();
}
