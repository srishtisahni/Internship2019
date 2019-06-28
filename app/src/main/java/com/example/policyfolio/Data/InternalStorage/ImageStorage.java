package com.example.policyfolio.Data.InternalStorage;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.policyfolio.Util.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImageStorage {
    private static ImageStorage INSTANCE;
    private ContextWrapper wrapper;

    private ImageStorage(Context context){
        wrapper = new ContextWrapper(context);
    }

    public static ImageStorage getInstance(Context context){
        if(INSTANCE==null)
            INSTANCE=new ImageStorage(context);
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    public String save(String uId, String filename, Bitmap bmp){
        File file=wrapper.getDir(Constants.Documents.IMAGE_DIRECTORY,Context.MODE_PRIVATE);
        file=new File(file,uId+"_"+filename+".jpg");
        try {
            OutputStream stream=new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath();
    }

    public Bitmap fetch(String uId, String filename){
        File file=wrapper.getDir(Constants.Documents.IMAGE_DIRECTORY,Context.MODE_PRIVATE);
        file=new File(file,uId+"_"+filename+".jpg");
        String path=file.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }

    public Boolean delete(String uId, String filename) {
        File file=wrapper.getDir(Constants.Documents.IMAGE_DIRECTORY,Context.MODE_PRIVATE);
        file=new File(file,uId+"_"+filename+".jpg");
        boolean delete = file.delete();
        return delete;
    }
}
