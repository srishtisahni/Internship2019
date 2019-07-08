package com.example.policyfolio.data.firebase.classes;

public class LogInData {
    //Store Login Info in Shared Preference
    private boolean login;
    private int type;
    private String firebaseToken;

    public LogInData(boolean login, int type, String firebseToken){
        this.login = login;
        this.type = type;
        this.firebaseToken = firebseToken;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
