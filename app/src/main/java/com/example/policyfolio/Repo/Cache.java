package com.example.policyfolio.Repo;


import android.graphics.Bitmap;

import com.example.policyfolio.DataClasses.InsuranceProvider;
import com.example.policyfolio.DataClasses.Policy;
import com.example.policyfolio.DataClasses.User;

import java.util.HashMap;
import java.util.List;

public class Cache {
    //Stores hash maps of accesed information to improve performance and speed of accesss
    private HashMap<String, User> userMap;
    private HashMap<String, InsuranceProvider> companyMap;
    private HashMap<String, Policy> policyMap;
    private HashMap<String, Bitmap> imageMap;

    private Cache(){
        userMap = new HashMap<>();
        companyMap = new HashMap<>();
        policyMap = new HashMap<>();
        imageMap = new HashMap<>();
    }

    private static Cache INSTANCE;

    public static Cache getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Cache();
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    public void addUser(User user) {
        userMap.put(user.getId(),user);
    }

    public User getUser(String id) {
        return userMap.get(id);
    }

    public void addPolicies(List<Policy> result) {
        for(int i=0;i<result.size();i++)
            policyMap.put(result.get(i).getUserId(),result.get(i));
    }

    public void saveImage(String userId, String key, Bitmap bmp) {
        imageMap.put(userId+"_"+key,bmp);
    }
}
