package com.example.policyfolio.Data;

public class LoggedIn {
    private boolean login;
    private int type;
    private String firebseToken;

    public LoggedIn(boolean login,int type,String firebseToken){
        this.login = login;
        this.type = type;
        this.firebseToken = firebseToken;
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

    public String getFirebseToken() {
        return firebseToken;
    }

    public void setFirebseToken(String firebseToken) {
        this.firebseToken = firebseToken;
    }
}
