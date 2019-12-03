package com.example.policyfolio.data.local.classes;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Documents {
    @PrimaryKey @NonNull
    private String userId;

    private String adhaarNumber;
    private String adhaarLink;

    private String passportNumber;
    private String passportLink;

    private String panNumber;
    private String panLink;

    private String voterIdNumber;
    private String voterIdLink;

    private String drivingLicenseNumber;
    private String drivingLicenseLink;

    private String rationCardNumber;
    private String rationCardLink;

    public Documents(){

    }

    @Ignore
    public Documents(String userId, String adhaarNumber, String adhaarLink, String passportNumber, String passportLink,
                     String panNumber, String panLink, String voterIdNumber, String voterIdLink, String drivingLicenseNumber,
                     String drivingLicenseLink, String rationCardNumber, String rationCardLink){
        this.userId = userId;

        this.adhaarNumber = adhaarNumber;
        this.adhaarLink = adhaarLink;

        this.passportNumber = passportNumber;
        this.passportLink = passportLink;

        this.panNumber = panNumber;
        this.panLink = panLink;

        this.voterIdNumber = voterIdNumber;
        this.voterIdLink = voterIdLink;

        this.drivingLicenseNumber = drivingLicenseNumber;
        this.drivingLicenseLink = drivingLicenseLink;

        this.rationCardNumber = rationCardNumber;
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

    public String getAdhaarNumber() {
        return adhaarNumber;
    }

    public void setAdhaarNumber(String adhaarNumber) {
        this.adhaarNumber = adhaarNumber;
    }

    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getRationCardNumber() {
        return rationCardNumber;
    }

    public void setRationCardNumber(String rationCardNumber) {
        this.rationCardNumber = rationCardNumber;
    }

    public String getVoterIdNumber() {
        return voterIdNumber;
    }

    public void setVoterIdNumber(String voterIdNumber) {
        this.voterIdNumber = voterIdNumber;
    }
}
