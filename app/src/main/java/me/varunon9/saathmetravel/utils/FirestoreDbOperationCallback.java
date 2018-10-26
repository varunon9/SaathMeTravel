package me.varunon9.saathmetravel.utils;

public interface FirestoreDbOperationCallback {

    void onSuccess(Object object);
    void onFailure(Object object);
}
