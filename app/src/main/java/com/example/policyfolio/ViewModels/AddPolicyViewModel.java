package com.example.policyfolio.ViewModels;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.policyfolio.Data.Local.Classes.InsuranceProvider;
import com.example.policyfolio.Data.Local.Classes.Nominee;
import com.example.policyfolio.Data.Local.Classes.Notifications;
import com.example.policyfolio.Data.Local.Classes.Policy;
import com.example.policyfolio.Data.Repository;
import com.example.policyfolio.Util.Constants;
import com.facebook.login.LoginManager;

import java.util.HashMap;
import java.util.List;

public class AddPolicyViewModel extends ViewModel {
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
    private Bitmap bitmap;

    private HashMap<Integer,LiveData<List<InsuranceProvider>>> providers = new HashMap<>();
    private LiveData<List<Nominee>> nominees;
    private int loginType;

    public void initiateRepo(Context context){
        repository = Repository.getInstance(context);
    }

    public int getType() {
        return type;
    }

    public LiveData<List<InsuranceProvider>> setType(int type) {
        this.type = type;
        return getProviders();
    }

    private LiveData<List<InsuranceProvider>> getProviders() {
        if(providers.get(type) == null)
            providers.put(type,repository.fetchProviders(type));
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
            nominees = repository.fetchNominees(uId);
        return nominees;
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

    public LiveData<String> saveImage() {
        return repository.saveImage(bitmap,uId,policyNumber);
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
        Policy policy = new Policy(uId,policyNumber,provider.getId(), premiumDateEpoch, matureDateEpoch, premiumFrequency,premiumAmount,coverAmount,type,photoUrl,true,nominee.getEmail());
        return repository.addPolicy(policy);
    }

    public void setBitmap(Bitmap bmp) {
        this.bitmap = bmp;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }


    public LiveData<List<Notifications>> getAllNotificatios() {
        return repository.getAllNotifications();
    }

    public void deleteAllNotifications() {
        repository.deleteAllNotifications();
    }

    public LiveData<Boolean> logOut() {
        if(loginType == Constants.LoginInInfo.Type.FACEBOOK) {
            LoginManager.getInstance().logOut();
        }
        return repository.logOut(uId);
    }
}
