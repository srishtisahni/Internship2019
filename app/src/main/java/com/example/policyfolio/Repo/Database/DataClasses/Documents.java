package com.example.policyfolio.Repo.Database.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Documents {
    @PrimaryKey @NonNull
    private String userId;
    private String adhaarLink;
    private String passportLink;
    private String panLink;
    private String voterIdLink;
    private String drivingLicenseLink;
    private String rationCardLink;

    public Documents(){

    }

    @Ignore
    public Documents(String userId, String adhaarLink, String passportLink, String panLink, String voterIdLink, String drivingLicenseLink, String rationCardLink){
        this.userId = userId;
        this.adhaarLink = adhaarLink;
        this.passportLink = passportLink;
        this.panLink = panLink;
        this.voterIdLink = voterIdLink;
        this.drivingLicenseLink = drivingLicenseLink;
        this.rationCardLink = rationCardLink;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public String getAdhaarLink() {
        return adhaarLink;
    }

    public void setAdhaarLink(String adhaarLink) {
        this.adhaarLink = adhaarLink;
    }

    public String getDrivingLicenseLink() {
        return drivingLicenseLink;
    }

    public void setDrivingLicenseLink(String drivingLicenseLink) {
        this.drivingLicenseLink = drivingLicenseLink;
    }

    public String getPanLink() {
        return panLink;
    }

    public void setPanLink(String panLink) {
        this.panLink = panLink;
    }

    public String getPassportLink() {
        return passportLink;
    }

    public void setPassportLink(String passportLink) {
        this.passportLink = passportLink;
    }

    public String getRationCardLink() {
        return rationCardLink;
    }

    public void setRationCardLink(String rationCardLink) {
        this.rationCardLink = rationCardLink;
    }

    public String getVoterIdLink() {
        return voterIdLink;
    }

    public void setVoterIdLink(String voterIdLink) {
        this.voterIdLink = voterIdLink;
    }
}
