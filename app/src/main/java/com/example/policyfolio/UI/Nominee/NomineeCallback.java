package com.example.policyfolio.UI.Nominee;

import com.example.policyfolio.UI.Base.BaseActivityFragmentCallback;

public interface NomineeCallback extends BaseActivityFragmentCallback {
    void addNominee();
    void done();
    void showSnackbar(String text);
}
