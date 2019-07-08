package com.example.policyfolio.data.local.classes;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(
        indices = {
                @Index("userId"),
                @Index("pfId")
        }, primaryKeys = {"userId","email"})
public class Nominee {
    @NonNull
    private String userId;                  //Id of the user the nominee is associated to
    @NonNull
    private String pfId;                    //Policy Folio Id of the user
    private String name;
    private int relation;
    @NonNull
    private String email;
    private String phone;
    private String alternativeNumber;
    private Long lastUpdated;

    @Ignore
    public Nominee(String userId, String pfId, String name, int relation, String email, String phone, String alternativeNumber){
        this.userId = userId;
        this.pfId = pfId;
        this.name = name;
        this.relation = relation;
        this.email = email;
        this.phone = phone;
        this.alternativeNumber = alternativeNumber;
        setLastUpdated(System.currentTimeMillis()/1000);
    }
    public Nominee(){
        setLastUpdated(System.currentTimeMillis()/1000);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public String getAlternativeNumber() {
        return alternativeNumber;
    }

    public void setAlternativeNumber(String alternativeNumber) {
        this.alternativeNumber = alternativeNumber;
    }

    @NonNull
    public String getPfId() {
        return pfId;
    }

    public void setPfId(String pfId) {
        this.pfId = pfId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
