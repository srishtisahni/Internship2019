package com.example.policyfolio.Data;


public class LoggedIn {
    private boolean login;
    private int type;

    public LoggedIn(boolean login,int type){
        this.login = login;
        this.type = type;
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
}
