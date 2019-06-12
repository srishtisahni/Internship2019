package com.example.policyfolio.Repo.InternalStorage;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static com.example.policyfolio.Util.Constants.IMAGE_DIRECTORY;

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

    public String saveImage(String uId, String filename, Bitmap bmp){
        File file=wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE);
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

    public Bitmap fetchImage(String filename){
        File file=wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE);
        file=new File(file,filename+".jpg");
        String path=file.getAbsolutePath();
        Bitmap bmp= BitmapFactory.decodeFile(path);
        return bmp;
    }}
