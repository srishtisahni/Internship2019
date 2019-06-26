package com.example.policyfolio.Util.CallBackListeners;

public interface DocumentCallback {
    void uploadDocument(int type);
    void viewUploadedDocument(int type);
    void getImage();
    void done();
}
