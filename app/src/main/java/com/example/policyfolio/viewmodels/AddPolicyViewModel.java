package com.example.policyfolio.viewmodels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import com.example.policyfolio.data.local.classes.InsuranceProvider;
import com.example.policyfolio.data.local.classes.Nominee;
import com.example.policyfolio.data.local.classes.Policy;
import com.example.policyfolio.viewmodels.base.BaseViewModelNavigation;

import java.util.HashMap;
import java.util.List;

public class AddPolicyViewModel extends BaseViewModelNavigation {

    private int type = -1;
    private InsuranceProvider provider;
    private Nominee nominee;
    private String policyNumber;
    private int premiumFrequency;
    private String photoUrl;
    private Long premiumDateEpoch;
    private String premiumAmount;
    private String coverAmount;
    private Long matureDateEpoch;
    private Bitmap bitmap;

    private HashMap<Integer,LiveData<List<InsuranceProvider>>> providers = new HashMap<>();
    private LiveData<List<Nominee>> nominees;

    public int getType() {
        return type;
    }

    public LiveData<List<InsuranceProvider>> setType(int type) {
        this.type = type;
        return getProviders();
    }

    private LiveData<List<InsuranceProvider>> getProviders() {
        if(providers.get(type) == null)
            providers.put(type, getRepository().fetchProviders(type));
        return providers.get(type);
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
        if(nominees == null)
            nominees = getRepository().fetchNominees(getUid());
        return nominees;
    }

    public Nominee getNominee() {
        return nominee;
    }

    public void setNominee(Nominee nominee) {
        this.nominee = nominee;
    }

    public LiveData<String> saveImage() {
        return getRepository().saveImage(bitmap, getUid(),policyNumber);
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
        Policy policy = new Policy(getUid(),policyNumber,provider.getId(), premiumDateEpoch, matureDateEpoch, premiumFrequency,premiumAmount,coverAmount,type,photoUrl,true,nominee.getEmail());
        return getRepository().addPolicy(policy);
    }

    public void setBitmap(Bitmap bmp) {
        this.bitmap = bmp;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
