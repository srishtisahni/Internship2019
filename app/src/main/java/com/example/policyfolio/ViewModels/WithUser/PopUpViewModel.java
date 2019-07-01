package com.example.policyfolio.ViewModels.WithUser;

import androidx.lifecycle.LiveData;

import com.example.policyfolio.Data.Local.Classes.User;
import com.example.policyfolio.ViewModels.Base.BaseViewModelWithUser;

public class PopUpViewModel extends BaseViewModelWithUser {

    private User localUser = new User();

    public LiveData<Boolean> resetPassword() {
        return getRepository().resetPassword(localUser.getEmail());
    }

    public String getId() {
        return localUser.getId();
    }

    public void setId(String id){
        localUser.setId(id);
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

    public void updateUser(User user) {
        this.localUser = user;
    }

    public LiveData<Boolean> updateFirebaseUser() {
        localUser.setComplete(true);
        return getRepository().updateFirebaseUser(localUser);
    }

    public User getLocalUser() {
        return localUser;
    }
}
