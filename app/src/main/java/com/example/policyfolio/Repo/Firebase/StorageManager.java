package com.example.policyfolio.Repo.Firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.policyfolio.Util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
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
        StorageReference fileRef = storageReference.child(Constants.Documents.IMAGE_DIRECTORY+"/"+uId+"_"+filename);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = fileRef.putBytes(data);
        Log.e("UPLOAD",storageReference.getPath());
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

    public void fetchImage(String uId, String filename, final MutableLiveData<Bitmap> result) {
        StorageReference fileRef = storageReference.child(Constants.Documents.IMAGE_DIRECTORY+"/"+uId+"_"+filename);
        fileRef.getBytes(Constants.Documents.ONE_MEGABYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()){
                    byte[] bytes=task.getResult();
                    ByteArrayInputStream inputStream= new ByteArrayInputStream(bytes);
                    Bitmap bmp= BitmapFactory.decodeStream(inputStream);
                    result.setValue(bmp);
                }
            }
        });
    }

    public void deleteImage(String uId, String filename, final MutableLiveData<Boolean> result) {
        StorageReference fileRef = storageReference.child(Constants.Documents.IMAGE_DIRECTORY+"/"+uId+"_"+filename);
        fileRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    result.setValue(true);
                else
                    result.setValue(false);
            }
        });
    }
}
