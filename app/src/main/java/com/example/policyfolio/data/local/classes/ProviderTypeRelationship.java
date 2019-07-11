package com.example.policyfolio.data.local.classes;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity()
public class ProviderTypeRelationship {
    @NonNull @PrimaryKey(autoGenerate = true)
    private Long id;
    private long providerId;
    private int type;

    public ProviderTypeRelationship(){

    }

    @Ignore
    public ProviderTypeRelationship( Long providerId, int type){
        this.providerId = providerId;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }
}
