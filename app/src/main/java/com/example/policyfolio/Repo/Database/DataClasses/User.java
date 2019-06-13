package com.example.policyfolio.Repo.Database.DataClasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class User implements Parcelable {
    //User Information
    @NonNull
    @PrimaryKey
    private String id;
    private String email;
    private String phone;
    private String name;
    private Long birthday;
    private int gender = 0;
    private String city;
    private Integer type;
    private boolean complete;
    private String firstName;
    private String lastName;

    @Ignore
    public User(String id, String email, String phone, String name, Long birthday, int gender, String city,int type){
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
    }

    public User() {
        setComplete();
    }

    protected User(Parcel in) {
        id = in.readString();
        email = in.readString();
        phone = in.readString();
        name = in.readString();
        city = in.readString();
        if (in.readByte() == 0) {
            birthday = null;
        } else {
            birthday = in.readLong();
        }
        gender = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        complete = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
        this.name = firstName+" "+lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.name = firstName+" "+lastName;
    }

    private void setComplete() {
        this.complete = email!=null && id!=null && phone!=null && name!=null && birthday!=null && city!=null && type!=null;
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
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeByte((byte) (complete ? 1 : 0));
    }
}
