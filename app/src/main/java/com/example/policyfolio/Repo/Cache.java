package com.example.policyfolio.Repo;


import com.example.policyfolio.DataClasses.Company;
import com.example.policyfolio.DataClasses.Policy;
import com.example.policyfolio.DataClasses.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cache {
    private HashMap<String, User> userMap;
    private HashMap<String, Company> companyMap;
    private HashMap<String, Policy> policyMap;

    private Cache(){
        userMap = new HashMap<>();
        companyMap = new HashMap<>();
        policyMap = new HashMap<>();
    }

    private static Cache INSTANCE;

    public static Cache getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Cache();
        return INSTANCE;
    }

    public void addUser(User user) {
        userMap.put(user.getId(),user);
    }

    public User getUser(String id) {
        return userMap.get(id);
    }

    public void addPolicies(List<Policy> result) {
        for(int i=0;i<result.size();i++)
            policyMap.put(result.get(i).getId(),result.get(i));
    }
}
