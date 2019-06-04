package com.example.policyfolio.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.policyfolio.DataClasses.User;
import com.example.policyfolio.Repo.Repository;
import com.google.firebase.auth.FirebaseUser;

public class HomeViewModel extends ViewModel {

    private int type;
    private MutableLiveData<User> user = new MutableLiveData<>();
    private Repository repository = Repository.getInstance();

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setUid(String firebaseToken) {
        user.setValue(new User(firebaseToken));
    }

    public String getUid() {
        return user.getValue().getId();
    }

    public LiveData<User> fetchUser() {
        return repository.fetchUser(user.getValue().getId());
    }

    public void updateUser(User user) {
        this.user.setValue(user);
        repository.updateUser(user);
    }

    public LiveData<User> getUser() {
        return user;
    }
}
