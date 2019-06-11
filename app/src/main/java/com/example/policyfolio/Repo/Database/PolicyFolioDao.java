package com.example.policyfolio.Repo.Database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.policyfolio.DataClasses.InsuranceProvider;
import com.example.policyfolio.DataClasses.Policy;
import com.example.policyfolio.DataClasses.User;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PolicyFolioDao {
    @Query("SELECT * from User where id = :id")
    LiveData<User> getUser(String id);                                  //Fetch User based on id    (Firebase Auth id)

    @Query("SELECT * from User where email = :email")
    LiveData<User> getUserFromEmail(String email);                      //Fetch User based on Email

    @Query("SELECT * from User where phone = :phone")
    LiveData<User> getUserFromPhone(String phone);                      //Fetch User based on Phone Number (Also saved as a string)

    @Query("SELECT * from Policy where userId = :userId")
    LiveData<List<Policy>> getPolicies(String userId);                  //Fetch Policies belonging to a particular user

    @Query("SELECT * from InsuranceProvider where id = :id")
    LiveData<InsuranceProvider> getProvider(Long id);                   //Fetch Insurance Provider based on the provide Id (Self generated long)

    @Query("SELECT * from InsuranceProvider where type = :type")
    LiveData<List<InsuranceProvider>> getProvidersFromType(int type);   //Fetch Insurance Providers of a certain type

    @Insert(onConflict = REPLACE)
    void putUser(User user);                                            //Update or Add user to the local database

    @Insert(onConflict = REPLACE)
    void putPolicy(Policy policy);                                      //Update or Add a Policy to the local database

    @Insert(onConflict = REPLACE)
    void putPolicies(List<Policy> policies);                            //Add multiple Policies at once

    @Insert(onConflict = REPLACE)
    void putProvider(InsuranceProvider insuranceProvider);              //Add an Insurance Provider to the local database

    @Insert(onConflict = REPLACE)
    void putProviders(List<InsuranceProvider> providers);               //Add multiple Insurance Providers to the local database
}
