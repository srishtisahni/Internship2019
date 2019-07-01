package com.example.policyfolio.ViewModels.WithUser;

import androidx.lifecycle.LiveData;

import com.example.policyfolio.ViewModels.Base.BaseViewModelWithUser;

public class PopUpViewModel extends BaseViewModelWithUser {


    public LiveData<Boolean> updateFirebaseUser() {
        setComplete(true);
        return getRepository().updateFirebaseUser(getLocalUser());
    }
}
