package com.example.policyfolio.ui.document;

public interface DocumentCallback {
    void uploadDocument(int type);
    void viewUploadedDocument(int type);
    void getImage();
    void done(int type, boolean uploaded);
    void showSnackbar(String text);
}
