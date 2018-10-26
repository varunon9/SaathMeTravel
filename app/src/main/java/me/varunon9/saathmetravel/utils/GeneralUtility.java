package me.varunon9.saathmetravel.utils;

import android.location.Location;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

import me.varunon9.saathmetravel.models.User;

public class GeneralUtility {

    public User convertFirebaseUserToUser(FirebaseUser firebaseUser, Location userLocation) {
        User user = new User();

        user.setEmail(firebaseUser.getEmail());

        if (userLocation != null) {
            GeoPoint geoPoint = new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
            user.setLocation(geoPoint);
        }
        user.setMobile(firebaseUser.getPhoneNumber());
        user.setName(firebaseUser.getDisplayName());
        user.setUid(firebaseUser.getUid());
        if (firebaseUser.getPhotoUrl() != null) {
            user.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
        }

        return user;
    }
}
