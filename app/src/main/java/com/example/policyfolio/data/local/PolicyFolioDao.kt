package com.example.policyfolio.data.local


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.policyfolio.data.local.classes.Documents
import com.example.policyfolio.data.local.classes.InsuranceProducts
import com.example.policyfolio.data.local.classes.InsuranceProvider
import com.example.policyfolio.data.local.classes.Nominee
import com.example.policyfolio.data.local.classes.Notifications
import com.example.policyfolio.data.local.classes.Policy
import com.example.policyfolio.data.local.classes.ProviderTypeRelationship
import com.example.policyfolio.data.local.classes.User

import java.util.ArrayList

import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface PolicyFolioDao {

    @get:Query("SELECT * from InsuranceProvider")
    val providers: LiveData<MutableList<InsuranceProvider>>                         //Fetch All Insurance Providers

    @get:Query("SELECT * from Notifications")
    val allNotifications: LiveData<MutableList<Notifications>>

    @Query("SELECT * from User where id = :id")
    fun getUser(id: String): LiveData<User>                                   //Fetch User based on id    (Firebase Auth id)

    @Query("SELECT * from User where email = :email")
    fun getUserFromEmail(email: String): LiveData<User>                       //Fetch User based on Email

    @Query("SELECT * from User where phone = :phone")
    fun getUserFromPhone(phone: String): LiveData<User>                       //Fetch User based on Phone Number (Also saved as a string)

    @Query("SELECT * from Policy where userId = :userId")
    fun getPolicies(userId: String): LiveData<MutableList<Policy>>                   //Fetch Policies belonging to a particular user

    @Query("SELECT * from Policy where userId = :userId AND nominee = :email")
    fun getPoliciesForNominee(userId: String, email: String): LiveData<MutableList<Policy>>

    @Query("SELECT * from InsuranceProvider where id = :id")
    fun getProvider(id: Long?): LiveData<InsuranceProvider>                    //Fetch Insurance Provider based on the provide Id (Self generated long)

    @Query("SELECT * from InsuranceProvider where id in (SELECT providerId from ProviderTypeRelationship where type = :type)")
    fun getProvidersFromType(type: Int): LiveData<MutableList<InsuranceProvider>>    //Fetch Insurance Providers of a certain type

    @Query("SELECT * from InsuranceProducts where type = :type")
    fun getProductsFromType(type: Int): LiveData<MutableList<InsuranceProducts>>

    @Query("SELECT * from Nominee where userId = :id")
    fun getNomineesForUser(id: String): LiveData<MutableList<Nominee>>               //Fetch Nominees for a particular User

    @Query("SELECT * from Documents where userId = :uId")
    fun getDocuments(uId: String): LiveData<Documents>

    @Query("SELECT * from Notifications where policyNumber = :policyNumber")
    fun getNotifications(policyNumber: String): LiveData<MutableList<Notifications>>

    @Query("SELECT id from ProviderTypeRelationship where providerId = :providerId and type = :type")
    fun getRelationId(providerId: Long?, type: Int): List<Long>

    @Query("DELETE from Notifications where policyNumber = :policyNumber")
    fun deleteNotifications(policyNumber: String)                       //Deletes Notifications for a policy

    @Query("DELETE from Notifications where id = :id")
    fun deleteNotifications(id: Long)

    @Query("DELETE from Notifications")
    fun deleteAllNotifications()                                       //Deletes All Notifications

    @Insert(onConflict = REPLACE)
    fun putUser(user: User)                                             //Update or Add user to the local database

    @Insert(onConflict = REPLACE)
    fun putPolicy(policy: Policy)                                       //Update or Add a Policy to the local database

    @Insert(onConflict = REPLACE)
    fun putPolicies(policies: List<Policy>)                             //Add multiple Policies at once

    @Insert(onConflict = REPLACE)
    fun putProvider(insuranceProvider: InsuranceProvider)               //Add an Insurance Provider to the local database

    @Insert(onConflict = REPLACE)
    fun putProviders(providers: List<InsuranceProvider>)                //Add multiple Insurance Providers to the local database

    @Insert(onConflict = REPLACE)
    fun putNominee(nominee: Nominee)                                    //Add a Nominee to the local database

    @Insert(onConflict = REPLACE)
    fun putNominees(nominees: List<Nominee>)                            //Add multiple Nominees to the local database

    @Insert(onConflict = REPLACE)
    fun putNotifications(notifications: List<Notifications>): List<Long>             //Add notifications to database

    @Insert(onConflict = REPLACE)
    fun putDocuments(documents: Documents)

    @Insert(onConflict = REPLACE)
    fun putInsuranceProducts(products: List<InsuranceProducts>)

    @Insert(onConflict = REPLACE)
    fun putProviderRelationship(relationship: ProviderTypeRelationship)
}
