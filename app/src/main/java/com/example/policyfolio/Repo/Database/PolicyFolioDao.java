package com.example.policyfolio.Repo.Database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.policyfolio.Repo.Database.DataClasses.InsuranceProvider;
import com.example.policyfolio.Repo.Database.DataClasses.Nominee;
import com.example.policyfolio.Repo.Database.DataClasses.Notifications;
import com.example.policyfolio.Repo.Database.DataClasses.Policy;
import com.example.policyfolio.Repo.Database.DataClasses.User;
import com.example.policyfolio.Util.Constants;

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

    @Query("SELECT * from InsuranceProvider")
    LiveData<List<InsuranceProvider>> getProviders();                         //Fetch All Insurance Providers

    @Query("SELECT * from InsuranceProvider where type = :type")
    LiveData<List<InsuranceProvider>> getProvidersFromType(int type);   //Fetch Insurance Providers of a certain type

    @Query("SELECT * from Nominee where userId = :id")
    LiveData<List<Nominee>> getNomineesForUser(String id);              //Fetch Nominees for a particular User

    @Query("SELECT * from Notifications where policyNumber = :policyNumber")
    LiveData<List<Notifications>> getNotifications(String policyNumber);

    @Query("SELECT * from Notifications")
    LiveData<List<Notifications>> getAllNotifications();

    @Query("DELETE from Notifications where policyNumber = :policyNumber")
    void deleteNotifications(String policyNumber);                      //Deletes Notifications for a policy

    @Query("DELETE from Notifications where id = :id")
    void deleteNotifications(long id);

    @Query("DELETE from Notifications")
    void deleteAllNotifications();                                      //Deletes All Notifications

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

    @Insert(onConflict = REPLACE)
    void putNominee(Nominee nominee);                                   //Add a Nominee to the local database

    @Insert(onConflict = REPLACE)
    void putNominees(List<Nominee> nominees);                           //Add multiple Nominees to the local database

    @Insert(onConflict = REPLACE)
    List<Long> putNotifications(List<Notifications> notifications);            //Add notifications to database

}
