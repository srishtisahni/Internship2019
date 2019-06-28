package com.example.policyfolio.Data.Local.Classes;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class User {
    //User Information
    @NonNull
    @PrimaryKey
    private String id;
    private String email;
    private String phone;
    private String name;
    private long birthday;
    private int gender = 0;
    private String city;
    private Integer type;
    private boolean complete;
    private String firstName;
    private String lastName;
    private Long lastUpdated;

    @Ignore
    public User(String id, String email, String phone, String name, long birthday, int gender, String city,int type){
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.city = city;
        this.type = type;
        setComplete();
        splitName();
        setLastUpdated(System.currentTimeMillis()/1000);
    }

    private void splitName() {
        lastName = "";
        firstName = "";
        if(name!=null) {
            if (name.split("\\w+").length > 1) {
                lastName = name.substring(name.lastIndexOf(" ") + 1);
                firstName = name.substring(0, name.lastIndexOf(' '));
            } else {
                firstName = name;
            }
        }
    }

    @Ignore
    public User(String id) {
        this.id = id;
        setComplete();
        setLastUpdated(System.currentTimeMillis()/1000);
    }

    public User() {
        setComplete();
        setLastUpdated(System.currentTimeMillis()/1000);
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String uid) {
        id = uid;
        setComplete();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        setComplete();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        setComplete();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setComplete();
        splitName();
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
        setComplete();
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
        setComplete();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        setComplete();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    private void setComplete() {
        this.complete = email!=null && id!=null && phone!=null && name!=null && birthday>0 && city!=null && type!=null;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
