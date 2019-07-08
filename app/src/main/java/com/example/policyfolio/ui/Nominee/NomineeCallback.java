package com.example.policyfolio.ui.Nominee;

import com.example.policyfolio.ui.base.BaseActivityFragmentCallback;

public interface NomineeCallback extends BaseActivityFragmentCallback {
    void addNominee();
    void done();
    void showSnackbar(String text);
}
