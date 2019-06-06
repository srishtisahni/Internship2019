package com.example.policyfolio.Repo.Database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.policyfolio.DataClasses.Company;
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

    @Query("SELECT * from Policy where id = :userId")
    LiveData<List<Policy>> getPolicies(String userId);

    @Query("SELECT * from Company where id = :company")
    LiveData<Company> getComapny(String company);

    @Insert(onConflict = REPLACE)
    void putUser(User user);

    @Insert(onConflict = REPLACE)
    void putPolicy(Policy policy);

    @Insert(onConflict = REPLACE)
    void putPolicies(List<Policy> policies);

    @Insert(onConflict = REPLACE)
    void putCompany(Company company);
}
