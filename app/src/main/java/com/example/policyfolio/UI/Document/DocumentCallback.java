package com.example.policyfolio.UI.Document;

public interface DocumentCallback {
    void uploadDocument(int type);
    void viewUploadedDocument(int type);
    void getImage();
    void done(int type, boolean uploaded);
    void showSnackbar(String text);
}
