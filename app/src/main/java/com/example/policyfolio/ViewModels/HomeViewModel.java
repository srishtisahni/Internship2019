package com.example.policyfolio.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.DataClasses.User;
import com.example.policyfolio.Repo.Repository;

public class HomeViewModel extends ViewModel {

    private int type;
    private MutableLiveData<User> user = new MutableLiveData<>();
    private Repository repository;

    public void initiateRepo(Context context) {
        repository = Repository.getInstance(context);
    }

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
