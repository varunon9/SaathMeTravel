package me.varunon9.saathmetravel.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.List;
import java.util.Map;

import me.varunon9.saathmetravel.constants.AppConstants;

public class FirestoreDbUtility {

    private FirebaseFirestore db;
    private String TAG = "FirestoreDbUtility";

    public FirestoreDbUtility() {
        db = FirebaseFirestore.getInstance();
    }

    public void createOrMerge(final String collectionName, final String documentName,
                              Object object, final FirestoreDbOperationCallback callback) {
        try {
            db.collection(collectionName).document(documentName)
                    .set(object, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "createOrMerge success: "
                                    + collectionName
                                    + " "
                                    + documentName);
                            callback.onSuccess(null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "createOrMerge failure: "
                                    + collectionName
                                    + " "
                                    + documentName);
                            e.printStackTrace();
                            callback.onFailure(null);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(final String collectionName, final String documentName,
                       final Map<String, Object> hashMap,
                       final FirestoreDbOperationCallback callback) {

        try {
            // overriding updatedAt column to hashMap for all collections
            hashMap.put("updatedAt", new Date());

            db.collection(collectionName).document(documentName)
                    .update(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "update success: "
                                    + collectionName
                                    + " "
                                    + documentName
                                    + " "
                                    + hashMap.toString()
                            );
                            callback.onSuccess(null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "update failure: "
                                    + collectionName
                                    + " "
                                    + documentName);
                            e.printStackTrace();
                            callback.onFailure(null);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getOne(final String collectionName, final String documentName,
                       final FirestoreDbOperationCallback callback) {
        try {
            db.collection(collectionName).document(documentName)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            callback.onSuccess(documentSnapshot);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onFailure(null);
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMany(final String collectionName,
                        final List<FirestoreQuery> queryList,
                        final FirestoreDbOperationCallback callback) {

        try {
            CollectionReference collectionReference = db.collection(collectionName);
            Query query = null;
            for (FirestoreQuery firestoreQuery: queryList) {
                switch (firestoreQuery.getConditionCode()) {
                    case FirestoreQueryConditionCode.WHERE_LESS_THAN: {
                        if (query == null) {
                            query = collectionReference.whereLessThan(
                                    firestoreQuery.getField(),
                                    firestoreQuery.getValue()
                            );
                        } else {
                            query = query.whereLessThan(
                                    firestoreQuery.getField(),
                                    firestoreQuery.getValue()
                            );
                        }
                        break;
                    }

                    case FirestoreQueryConditionCode.WHERE_EQUAL_TO: {
                        if (query == null) {
                            query = collectionReference.whereEqualTo(
                                    firestoreQuery.getField(),
                                    firestoreQuery.getValue()
                            );
                        } else {
                            query = query.whereEqualTo(
                                    firestoreQuery.getField(),
                                    firestoreQuery.getValue()
                            );
                        }
                        break;
                    }

                    case FirestoreQueryConditionCode.WHERE_GREATER_THAN: {
                        if (query == null) {
                            query = collectionReference.whereGreaterThan(
                                    firestoreQuery.getField(),
                                    firestoreQuery.getValue()
                            );
                        } else {
                            query = query.whereGreaterThan(
                                    firestoreQuery.getField(),
                                    firestoreQuery.getValue()
                            );
                        }
                        break;
                    }

                    case FirestoreQueryConditionCode.WHERE_LESS_THAN_OR_EQUAL_TO: {
                        if (query == null) {
                            query = collectionReference.whereLessThanOrEqualTo(
                                    firestoreQuery.getField(),
                                    firestoreQuery.getValue()
                            );
                        } else {
                            query = query.whereLessThanOrEqualTo(
                                    firestoreQuery.getField(),
                                    firestoreQuery.getValue()
                            );
                        }
                        break;
                    }

                    case FirestoreQueryConditionCode.WHERE_GREATER_THAN_OR_EQUAL_TO: {
                        if (query == null) {
                            query = collectionReference.whereGreaterThanOrEqualTo(
                                    firestoreQuery.getField(),
                                    firestoreQuery.getValue()
                            );
                        } else {
                            query = query.whereGreaterThanOrEqualTo(
                                    firestoreQuery.getField(),
                                    firestoreQuery.getValue()
                            );
                        }
                        break;
                    }
                }
            }

            Task<QuerySnapshot> task = null;
            if (query == null) {
                task = collectionReference.get();
            } else {
                task = query.get();
            }

            task
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            callback.onSuccess(querySnapshot);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onFailure(null);
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
