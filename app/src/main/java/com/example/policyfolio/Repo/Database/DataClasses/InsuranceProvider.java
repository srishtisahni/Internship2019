package com.example.policyfolio.Repo.Database.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class InsuranceProvider {
    //Insurance Providers
    @NonNull @PrimaryKey(autoGenerate = true)
    private Long id;
    private String name;
    private int type;
    private String photoUrl;
    private Long lastUpdated;

    public InsuranceProvider(){
        setLastUpdated(System.currentTimeMillis()/1000);
    }

    @Ignore
    public InsuranceProvider(Long id, String name, int type, String photoUrl){
        this.id = id;
        this.name = name;
        this.type = type;
        this.photoUrl = photoUrl;
        setLastUpdated(System.currentTimeMillis()/1000);
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
