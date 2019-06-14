package com.example.policyfolio.ViewModels.NavigationViewModels;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Repo.Database.DataClasses.InsuranceProvider;
import com.example.policyfolio.Repo.Database.DataClasses.Nominee;
import com.example.policyfolio.Repo.Database.DataClasses.Policy;
import com.example.policyfolio.Repo.Repository;

import java.util.List;

public class AddViewModel extends ViewModel {
    private Repository repository;

    private int type;
    private String uId;
    private InsuranceProvider provider;
    private Nominee nominee;
    private String policyNumber;
    private int premiumFrequency;
    private String photoUrl;
    private Long premiumDateEpoch;
    private String premiumAmount;
    private String coverAmount;
    private Long matureDateEpoch;

    public void initiateRepo(Context context){
        repository = Repository.getInstance(context);
    }

    public int getType() {
        return type;
    }

    public LiveData<List<InsuranceProvider>> setType(int type) {
        this.type = type;
        return repository.fetchProviders(type);
    }

    public void setProvider(InsuranceProvider provider) {
        this.provider = provider;
    }

    public InsuranceProvider getProvider() {
        return provider;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public int getPremiumFrequency() {
        return premiumFrequency;
    }

    public void setPremiumFrequency(int premiumFrequency) {
        this.premiumFrequency = premiumFrequency;
    }

    public LiveData<List<Nominee>> fetchNominees() {
        return repository.fetchNominees(uId);
    }

    public String getuId() {
        return uId;
    }

    public void setUid(String uId) {
        this.uId = uId;
    }

    public Nominee getNominee() {
        return nominee;
    }

    public void setNominee(Nominee nominee) {
        this.nominee = nominee;
    }

    public LiveData<String> saveImage(Bitmap bmp) {
        return repository.saveImage(bmp,uId,policyNumber);
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Long getPremiumDateEpoch() {
        return premiumDateEpoch;
    }

    public void setPremiumDateEpoch(Long premiumDateEpoch) {
        this.premiumDateEpoch = premiumDateEpoch;
    }

    public Long getMatureDateEpoch() {
        return matureDateEpoch;
    }

    public void setMatureDateEpoch(Long matureDateEpoch) {
        this.matureDateEpoch = matureDateEpoch;
    }

    public String getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(String premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public String getCoverAmount() {
        return coverAmount;
    }

    public void setCoverAmount(String coverAmount) {
        this.coverAmount = coverAmount;
    }

    public LiveData<Boolean> savePolicy() {
        Policy policy = new Policy(uId,policyNumber,provider.getId(), premiumDateEpoch, matureDateEpoch, premiumFrequency,premiumAmount,coverAmount,type,photoUrl);
        return repository.addPolicy(policy);
    }
}
