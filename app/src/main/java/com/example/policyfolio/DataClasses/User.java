package com.example.policyfolio.DataClasses;

public class User {

    private String id;
    private String email;
    private String phone;
    private String name;
    private Long birthday;
    private int gender;
    private String city;

    public User(String id, String email, String phone, String name, Long birthday, int gender, String city){
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String uid) {
        id = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
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
