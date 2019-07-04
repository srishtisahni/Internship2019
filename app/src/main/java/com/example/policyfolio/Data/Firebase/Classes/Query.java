package com.example.policyfolio.Data.Firebase.Classes;

public class Query {
    private String userId;
    private String query;
    private boolean resolved;
    private int type;

    public Query(String userId,String query, int type, boolean resolved){
        this.userId = userId;
        this.query = query;
        this.type = type;
        this.resolved = resolved;
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

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
}
