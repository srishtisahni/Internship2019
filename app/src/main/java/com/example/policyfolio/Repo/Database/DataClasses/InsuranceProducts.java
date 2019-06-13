package com.example.policyfolio.Repo.Database.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        indices = {
            @Index("providerId")
        })
public class InsuranceProducts {
    //Insurance Products
    @PrimaryKey @NonNull
    private String id;
    private Long providerId;
    private String name;

    @Ignore
    public InsuranceProducts(String id, Long providerId, String name){
        this.id = id;
        this.providerId = providerId;
        this.name = name;
    }

    public InsuranceProducts(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }
}
