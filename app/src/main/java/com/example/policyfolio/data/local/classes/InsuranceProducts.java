package com.example.policyfolio.data.local.classes;

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
    private int type;
    private String name;
    private Long lastUpdated;
    private Integer frequency;
    private String premium;
    private String sumAssured;

    @Ignore
    public InsuranceProducts(String id, Long providerId, String name, Integer frequency, String premium, String sumAssured, int type){
        this.id = id;
        this.providerId = providerId;
        this.name = name;
        this.frequency = frequency;
        this.premium = premium;
        this.sumAssured = sumAssured;
        this.type = type;
        setLastUpdated(System.currentTimeMillis()/1000);
    }

    public InsuranceProducts(){
        setLastUpdated(System.currentTimeMillis()/1000);
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

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public String getSumAssured() {
        return sumAssured;
    }

    public void setSumAssured(String sumAssured) {
        this.sumAssured = sumAssured;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
