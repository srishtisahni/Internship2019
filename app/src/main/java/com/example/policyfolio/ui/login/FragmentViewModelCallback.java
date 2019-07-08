package com.example.policyfolio.ui.login;

import android.content.Intent;

public interface FragmentViewModelCallback {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
