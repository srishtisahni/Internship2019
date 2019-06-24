package com.example.policyfolio.Repo.Firebase.DataClasses;

public class Query {
    private String userId;
    private String query;
    private int type;

    public Query(String userId,String query, int type){
        this.userId = userId;
        this.query = query;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
