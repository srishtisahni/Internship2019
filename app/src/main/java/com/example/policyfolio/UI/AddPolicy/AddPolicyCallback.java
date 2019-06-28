package com.example.policyfolio.UI.AddPolicy;

public interface AddPolicyCallback {
    void next();
    void done();
    void addPolicyImage();
    void clickPolicyImage();
    void showSnackbar(String text);
}
