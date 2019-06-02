package com.example.policyfolio.Data;

import java.util.Date;

public class User {
    private String email;
    private long phone;
    private String password;
    private String name;
    private Date birthday;
    private int gender;
    private String city;

    public User(String email, String password, String name, Date birthday, int gender, String city){
        this.email = email;
        this.phone = -1;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.city = city;
    }

    public User(long phone, String password, String name, Date birthday, int gender, String city){
        this.email = null;
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.city = city;
    }

    public User(String email, long phone, String password, String name, Date birthday, int gender, String city){
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
