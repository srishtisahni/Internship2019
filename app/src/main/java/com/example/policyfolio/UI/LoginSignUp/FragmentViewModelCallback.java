package com.example.policyfolio.UI.LoginSignUp;

import android.content.Intent;

public interface FragmentViewModelCallback {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
