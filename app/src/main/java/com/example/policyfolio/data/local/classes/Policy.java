package com.example.policyfolio.data.local.classes;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(
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
    private Long nextDueDate;
    private Long matureDate;
    private Integer frequency;
    private String premium;
    private String sumAssured;
    private Boolean paid;
    @NonNull
    private int type;
    private String documentUrl;
    private Long lastUpdated;
    @NonNull
    private String nominee;

    public Policy(){
        setLastUpdated(System.currentTimeMillis()/1000);
    }

    @Ignore
    public Policy(String id, String pNum, Long insuranceProviderId, Long nextDueDate, Long matureDate, Integer frequency, String premium, String sumAssured, int type, String documentUrl, Boolean paid, String nominee){
        this.userId = id;
        this.policyNumber = pNum;
        this.insuranceProviderId = insuranceProviderId;
        this.nextDueDate = nextDueDate;
        this.matureDate = matureDate;
        this.frequency = frequency;
        this.premium = premium;
        this.sumAssured = sumAssured;
        this.type = type;
        this.documentUrl = documentUrl;
        this.paid = paid;
        this.nominee = nominee;
        setLastUpdated(System.currentTimeMillis()/1000);
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

    public Long getMatureDate() {
        return matureDate;
    }

    public void setMatureDate(Long matureDate) {
        this.matureDate = matureDate;
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

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public String getSumAssured() {
        return sumAssured;
    }

    public void setSumAssured(String sumAssured) {
        this.sumAssured = sumAssured;
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

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    @NonNull
    public String getNominee() {
        return nominee;
    }

    public void setNominee(@NonNull String nominee) {
        this.nominee = nominee;
    }

    @Ignore
    public static Double totalCover(List<Policy> policies){
        Double cover = Double.valueOf(0);
        for(int i=0;i<policies.size();i++) {
            try {
                cover = cover + Double.parseDouble(policies.get(i).getSumAssured());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return cover;
    }
}
