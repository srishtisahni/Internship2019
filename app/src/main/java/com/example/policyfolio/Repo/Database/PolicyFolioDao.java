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
    LiveData<User> getUser(String id);

    @Query("SELECT * from User where email = :email")
    LiveData<User> getUserFromEmail(String email);

    @Query("SELECT * from User where phone = :phone")
    LiveData<User> getUserFromPhone(String phone);

    @Query("SELECT * from Policy where userId = :userId")
    LiveData<List<Policy>> getPolicies(String userId);

    @Query("SELECT * from InsuranceProvider where id = :id")
    LiveData<InsuranceProvider> getProvider(Long id);

    @Query("SELECT * from InsuranceProvider where type = :type")
    LiveData<List<InsuranceProvider>> getProvidersFromType(int type);

    @Insert(onConflict = REPLACE)
    void putUser(User user);

    @Insert(onConflict = REPLACE)
    void putPolicy(Policy policy);

    @Insert(onConflict = REPLACE)
    void putPolicies(List<Policy> policies);

    @Insert(onConflict = REPLACE)
    void putProvider(InsuranceProvider insuranceProvider);

    @Insert(onConflict = REPLACE)
    void putProviders(List<InsuranceProvider> providers);
}
