package com.example.policyfolio.viewmodels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.example.policyfolio.data.local.classes.Documents;
import com.example.policyfolio.util.Constants;
import com.example.policyfolio.viewmodels.base.BaseViewModelNavigation;

public class DocumentViewModel extends BaseViewModelNavigation {
    private Documents localCopy = new Documents();
    private LiveData<Documents> documents;
    private Bitmap[] image = new Bitmap[Constants.Documents.NUMBER];

    public LiveData<Documents> fetchDocument() {
        if(documents == null)
            documents = getRepository().fetchDocument(getUid());
        return documents;
    }

    public Bitmap getImage(int position) {
        return image[position];
    }

    public void setImage(Bitmap image,int position) {
        this.image[position] = image;
    }

    public LiveData<Boolean> deleteImage(int type) {
        return getRepository().deleteImage(getUid(), Constants.Documents.NAMES[type]);
    }

    public Documents getLocalCopy() {
        return localCopy;
    }

    public void setLocalCopy(Documents localCopy) {
        this.localCopy = localCopy;
    }

    public LiveData<Boolean> updateChanges() {
        return getRepository().addDocumentsVault(localCopy);
    }

    public LiveData<String> uploadImage(int type) {
        return getRepository().saveImage(image[type], getUid(),Constants.Documents.NAMES[type]);
    }

    public LiveData<Bitmap> fetchImage(int type) {
        return getRepository().fetchImage(getUid(),Constants.Documents.NAMES[type]);
    }
}