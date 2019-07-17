package com.example.policyfolio.viewmodels;

import androidx.lifecycle.LiveData;

import com.example.policyfolio.data.local.classes.Documents;
import com.example.policyfolio.data.local.classes.InsuranceProvider;
import com.example.policyfolio.data.local.classes.Notifications;
import com.example.policyfolio.data.local.classes.Policy;
import com.example.policyfolio.util.Constants;
import com.example.policyfolio.viewmodels.base.BaseViewModelNavigation;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends BaseViewModelNavigation {

    private LiveData<List<Policy>> policies;
    private LiveData<List<InsuranceProvider>> providers;

    public LiveData<List<Policy>> getPolicies() {
        if(policies == null)
            policies = getRepository().fetchPolicies(getUid());
        return policies;
    }

    public LiveData<List<InsuranceProvider>> getProviders() {
        if(providers == null)
            providers = getRepository().fetchAllProviders();
        return providers;
    }

    public LiveData<Boolean> updatePolicies(List<Policy> policies) {
        return getRepository().updatePolicies(getUid(), policies);
    }

    public LiveData<List<Long>> addNotifications(Notifications notification, int type) {
        ArrayList<Notifications> notifications = new ArrayList<>();
        if(type == Constants.Policy.Premium.PREMIUM_ANNUALLY)
            notifications.add(notification);
        notifications.add(notification);
        notifications.add(notification);
        notifications.add(notification);
        return getRepository().addNotifications(notifications);
    }

    public LiveData<List<Notifications>> getAllNotificatios() {
        return getRepository().getAllNotifications();
    }

    public void deleteAllNotifications() {
        getRepository().deleteAllNotifications();
    }

    public LiveData<Boolean> addDocumentsVault() {
        return getRepository().addDocumentsVault(new Documents(getUid(),null,null,null,
                null,null,null,null,null,null,
                null,null,null));
    }

    public LiveData<Boolean> updateFirebaseUser() {
        setComplete(true);
        return getRepository().updateFirebaseUser(getLocalUser());
    }
}
