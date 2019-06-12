package com.example.policyfolio.Util.CallBackListeners;

import android.content.Intent;

public interface FragmentViewModelCallback {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
