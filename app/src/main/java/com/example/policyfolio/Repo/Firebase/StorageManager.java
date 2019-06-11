package com.example.policyfolio.Repo.Firebase;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class StorageManager {

    private static StorageManager INSTANCE;
    private StorageReference storageReference;

    private StorageManager(){
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public static StorageManager getInstance() {
        if(INSTANCE==null)
            INSTANCE = new StorageManager();
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    public LiveData<String> saveImage(String uId, String filename, Bitmap bitmap) {
        final MutableLiveData<String> imagePath=new MutableLiveData<>();
        StorageReference fileRef = storageReference.child("images/"+uId+"_"+filename);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imagePath.setValue(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference storageReference=taskSnapshot.getStorage();
                imagePath.setValue(storageReference.toString());
            }
        });
        return imagePath;
    }
}
