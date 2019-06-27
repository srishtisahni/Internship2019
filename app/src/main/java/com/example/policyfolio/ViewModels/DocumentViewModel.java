package com.example.policyfolio.ViewModels;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Repo.Database.DataClasses.Documents;
import com.example.policyfolio.Repo.Database.DataClasses.Notifications;
import com.example.policyfolio.Repo.Repository;
import com.example.policyfolio.Util.Constants;
import com.facebook.login.LoginManager;

import java.util.List;

public class DocumentViewModel extends ViewModel {
    private Repository repository;
    private String uId;
    private Documents localCopy = new Documents();
    private LiveData<Documents> documents;
    private Bitmap[] image = new Bitmap[Constants.Documents.NUMBER];
    private int loginType;

    public void initiateRepo(Context context){
        repository = Repository.getInstance(context);
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public LiveData<Documents> fetchDocument() {
        if(documents == null)
            documents = repository.fetchDocument(uId);
        return documents;
    }

    public Bitmap getImage(int position) {
        return image[position];
    }

    public void setImage(Bitmap image,int position) {
        this.image[position] = image;
    }

    public LiveData<Boolean> deleteImage(int type) {
        return repository.deleteImage(uId, Constants.Documents.NAMES[type]);
    }

    public Documents getLocalCopy() {
        return localCopy;
    }

    public void setLocalCopy(Documents localCopy) {
        this.localCopy = localCopy;
    }

    public LiveData<Boolean> updateChanges() {
        return repository.addDocumentsVault(localCopy);
    }

    public LiveData<String> uploadImage(int type) {
        return repository.saveImage(image[type],uId,Constants.Documents.NAMES[type]);
    }

    public LiveData<Bitmap> fetchImage(int type) {
        return repository.fetchImage(uId,Constants.Documents.NAMES[type]);
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }


    public LiveData<List<Notifications>> getAllNotificatios() {
        return repository.getAllNotifications();
    }

    public void deleteAllNotifications() {
        repository.deleteAllNotifications();
    }

    public LiveData<Boolean> logOut() {
        if(loginType == Constants.LoginInInfo.Type.FACEBOOK) {
            LoginManager.getInstance().logOut();
        }
        return repository.logOut(uId);
    }
}