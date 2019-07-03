package com.example.policyfolio.UI.AddPolicy;

import com.example.policyfolio.UI.Base.BaseActivityFragmentCallback;

public interface AddPolicyCallback extends BaseActivityFragmentCallback {
    void next();
    void done();
    void addPolicyImage();
    void clickPolicyImage();
    void showSnackbar(String text);
}
