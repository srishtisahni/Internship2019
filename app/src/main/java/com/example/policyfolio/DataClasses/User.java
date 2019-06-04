package com.example.policyfolio.DataClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {

    private String id;
    private String email;
    private String phone;
    private String name;
    private Long birthday;
    private int gender = 0;
    private String city;
    private boolean complete;

    public User(String id, String email, String phone, String name, Long birthday, int gender, String city){
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.city = city;
        setComplete();
    }

    public User(String id) {
        this.id = id;
        setComplete();
    }

    public User() {
        setComplete();
    }

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

    public boolean isComplete() {
        return complete;
    }

    private void setComplete() {
        this.complete = email!=null && id!=null && phone!=null && name!=null && birthday!=null && city!=null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(email);
        parcel.writeString(phone);
        parcel.writeString(name);
        parcel.writeString(city);
        parcel.writeLong(birthday);
        parcel.writeInt(gender);
    }
}
