package com.example.policyfolio.ViewModels;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Repo.Database.DataClasses.User;
import com.example.policyfolio.Repo.Repository;

public class PopUpViewModel extends ViewModel {

    private User user = new User();

    private Repository repository;

    public void initiateRepo(Context context) {
        repository = Repository.getInstance(context);
    }

    public LiveData<Boolean> resetPassword() {
        return repository.resetPassword(user.getEmail());
    }

    public String getId() {
        return user.getId();
    }

    public void setId(String id){
        user.setId(id);
    }

    public String getEmail() {
        return user.getEmail();
    }

    public void setEmail(String email) {
        user.setEmail(email);
    }

    public String getName() {
        return user.getName();
    }

    public void setName(String name) {
        user.setName(name);
    }

    public String getCity() {
        return user.getCity();
    }

    public void setCity(String city) {
        user.setCity(city);
    }

    public int getGender() {
        return user.getGender();
    }

    public void setGender(int gender) {
        user.setGender(gender);
    }

    public Long getBirthday() {
        return user.getBirthday();
    }

    public void setBirthday(Long birthday) {
        user.setBirthday(birthday);
    }

    public String getPhone() {
        return user.getPhone();
    }

    public void setPhone(String phone) {
        user.setPhone(phone);
    }

    public LiveData<User> fetchUser(String id) {
        return repository.fetchUser(id);
    }

    public void updateUser(User user) {
        this.user = user;
    }

    public LiveData<Boolean> updateFirebaseUser() {
        return repository.updateFirebaseUser(user);
    }

    public User getUser() {
        return user;
    }
}
