package com.example.policyfolio.viewmodels.base;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.data.local.classes.Notifications;
import com.example.policyfolio.data.local.classes.User;
import com.example.policyfolio.data.Repository;
import com.example.policyfolio.util.Constants;
import com.facebook.login.LoginManager;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class BaseViewModelNavigation extends ViewModel {
    //Instance of the Repository
    private static Repository repository;

    //Binded Live Data
    private static LiveData<User> user;

    private static User localUser = new User();

    public void initiateRepo(Context context) {
        if(repository==null)
            repository = Repository.getInstance(context);
    }

    public LiveData<User> getUser() {
        if(user == null)
            user = repository.fetchUser(localUser.getId());
        return user;
    }

    public Repository getRepository() {
        return repository;
    }

    public LiveData<Boolean> logOut() {
        if(localUser.getType() == Constants.LoginInInfo.Type.FACEBOOK) {
            LoginManager.getInstance().logOut();
        }
        return getRepository().logOut(getUid());
    }


    public LiveData<List<Notifications>> getAllNotificatios() {
        return repository.getAllNotifications();
    }

    public void deleteAllNotifications() {
        repository.deleteAllNotifications();
    }

    public void setUid(String uId) {
        localUser.setId(uId);
    }

    public String getUid() {
        return localUser.getId();
    }

    public String getEmail() {
        return localUser.getEmail();
    }

    public void setEmail(String email) {
        localUser.setEmail(email);
    }

    public String getName() {
        return localUser.getName();
    }

    public void setName(String name) {
        localUser.setName(name);
    }

    public String getCity() {
        return localUser.getCity();
    }

    public void setCity(String city) {
        localUser.setCity(city);
    }

    public int getGender() {
        return localUser.getGender();
    }

    public void setGender(int gender) {
        localUser.setGender(gender);
    }

    public Long getBirthday() {
        return localUser.getBirthday();
    }

    public void setBirthday(Long birthday) {
        localUser.setBirthday(birthday);
    }

    public String getPhone() {
        return localUser.getPhone();
    }

    public void setPhone(String phone) {
        localUser.setPhone(phone);
    }

    public void setComplete(Boolean b){
        localUser.setComplete(b);
    }

    public boolean getComplete(){
        return localUser.isComplete();
    }

    public int getLoginType() {
        return localUser.getType();
    }

    public void setLoginType(int loginType) {
        localUser.setType(loginType);
    }

    public void updateUser(User user) {
        this.localUser = user;
    }

    public User getLocalUser() {
        return localUser;
    }

    public StorageReference getReference(String photoUrl, long providerId) {
        return repository.getPhotoReference(photoUrl,providerId);
    }
}
