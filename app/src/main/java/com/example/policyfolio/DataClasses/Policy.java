package com.example.policyfolio.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = InsuranceProvider.class,
                        parentColumns = "id",
                        childColumns = "insuranceProviderId"
                ),
        },
        indices = {
                @Index("userId"),
                @Index("insuranceProviderId")
        }
)
public class Policy {
    //Policy Information indexed using userId and Insurance Providers
    @NonNull
    private String userId;
    @NonNull @PrimaryKey
    private String policyNumber;
    @NonNull
    private Long insuranceProviderId;
    private Long startDate;
    private Long endDate;
    private Long nextDueDate;
    private Integer frequency;
    private Long premium;
    private Long sumAssured;
    @NonNull
    private int type;
    private String documentUrl;

    public Policy(){

    }

    @Ignore
    public Policy(String id, String pNum, Long insuranceProviderId, Long startDate, Long endDate, Long nextDueDate, Integer frequency, Long premium, Long sumAssured, int type, String documentUrl){
        this.userId = id;
        this.policyNumber = pNum;
        this.insuranceProviderId = insuranceProviderId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nextDueDate = nextDueDate;
        this.frequency = frequency;
        this.premium = premium;
        this.sumAssured = sumAssured;
        this.type = type;
        this.documentUrl = documentUrl;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(@NonNull String policyNumber) {
        this.policyNumber = policyNumber;
    }

    @NonNull
    public Long getInsuranceProviderId() {
        return insuranceProviderId;
    }

    public void setInsuranceProviderId(Long insuranceProviderId) {
        this.insuranceProviderId = insuranceProviderId;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(Long nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Long getPremium() {
        return premium;
    }

    public void setPremium(Long premium) {
        this.premium = premium;
    }

    public Long getSumAssured() {
        return sumAssured;
    }

    public void setSumAssured(Long sumAssured) {
        this.sumAssured = sumAssured;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    @NonNull
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
}
