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
                        childColumns = "id",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = Company.class,
                        parentColumns = "id",
                        childColumns = "company"
                ),
        },
        indices = {
                @Index("id"),
                @Index("company")
        }
)
public class Policy {
    @NonNull
    private String id;
    @NonNull @PrimaryKey
    private String policyNumber;
    @NonNull
    private Long company;
    private Long startDate;
    private Long endDate;
    private Long nextDueDate;
    private Long cycle;
    private Long premium;
    private Long sumAssured;
    @NonNull
    private int type;

    public Policy(){

    }

    @Ignore
    public Policy(String id, String pNum, Long company, Long startDate, Long endDate, Long nextDueDate, Long cycle, Long premium, Long sumAssured, int type){
        this.id = id;
        this.policyNumber = pNum;
        this.company = company;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nextDueDate = nextDueDate;
        this.cycle = cycle;
        this.premium = premium;
        this.sumAssured = sumAssured;
        this.type = type;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(@NonNull String policyNumber) {
        this.policyNumber = policyNumber;
    }

    @NonNull
    public Long getCompany() {
        return company;
    }

    public void setCompany(Long company) {
        this.company = company;
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

    public Long getCycle() {
        return cycle;
    }

    public void setCycle(Long cycle) {
        this.cycle = cycle;
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
}
