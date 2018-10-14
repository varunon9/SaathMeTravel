package me.varunon9.saathmetravel.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;

public class FirestoreDbUtility {

    private FirebaseFirestore db;
    private String TAG = "FirestoreDbUtility";

    public FirestoreDbUtility() {
        db = FirebaseFirestore.getInstance();
    }

    public void createOrMerge(final String collectionName, final String documentName,
                              Object object, final FirestoreDbOperationCallback callback) {
        db.collection(collectionName).document(documentName)
                .set(object, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "createOrMerge success: "
                                + collectionName
                                + " "
                                + documentName);
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "createOrMerge failure: "
                                + collectionName
                                + " "
                                + documentName);
                        callback.onFailure();
                    }
                });
    }

    public void update(final String collectionName, final String documentName,
                       Map hashMap, final FirestoreDbOperationCallback callback) {
        db.collection(collectionName).document(documentName)
                .update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "update success: "
                                + collectionName
                                + " "
                                + documentName);
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "update failure: "
                                + collectionName
                                + " "
                                + documentName);
                        callback.onFailure();
                    }
                });
    }
}
