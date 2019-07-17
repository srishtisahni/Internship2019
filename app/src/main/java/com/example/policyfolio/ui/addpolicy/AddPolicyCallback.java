package com.example.policyfolio.ui.addpolicy;

import com.example.policyfolio.ui.base.BaseActivityFragmentCallback;

public interface AddPolicyCallback extends BaseActivityFragmentCallback {
    void next();
    void done();
    void addPolicyImage();
    void clickPolicyImage();
    void showSnackbar(String text);
}
