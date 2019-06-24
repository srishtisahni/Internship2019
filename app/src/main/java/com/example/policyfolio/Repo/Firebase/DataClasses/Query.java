package com.example.policyfolio.Repo.Database.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        indices = {
                @Index("userId")
        }
)
public class Query {
    @PrimaryKey @NonNull
    private String id;
    @NonNull
    private String userId;
    private String query;
    @NonNull
    private int type;

    @Ignore
    public Query(String id,String userId,String query, int type){
        this.id = id;
        this.userId = userId;
        this.query = query;
        this.type = type;
    }

    public Query(){

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
