package me.varunon9.saathmetravel.utils;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import me.varunon9.saathmetravel.R;
import me.varunon9.saathmetravel.constants.AppConstants;
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

    /**
     * Latitude: 1 deg = 110.574 KM
     * Longitude: 1 deg = 111.320*cos(latitude) KM
     */
    public GeoPoint getLesserGeoPoint(Location location, int range) {
        LatLng latLng = AppConstants.DEFAULT_LAT_LNG;
        if (location != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        double lowerLatitude = latLng.latitude - (range * (1 / 110.574));
        double lowerLongitude = latLng.longitude - (range * (1 / 111.320));
        GeoPoint lesserGeoPoint = new GeoPoint(lowerLatitude, lowerLongitude);
        return lesserGeoPoint;
    }

    public GeoPoint getGreaterGeoPoint(Location location, int range) {
        LatLng latLng = AppConstants.DEFAULT_LAT_LNG;
        if (location != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        double greaterLatitude = latLng.latitude + (range * (1 / 110.574));
        double greaterLongitude = latLng.longitude + (range * (1 / 111.320));
        GeoPoint greaterGeoPoint = new GeoPoint(greaterLatitude, greaterLongitude);
        return greaterGeoPoint;
    }

    public void showTravellersOnMap(GoogleMap googleMap, QuerySnapshot querySnapshot) {
        if (googleMap != null) {
            googleMap.clear();
            LatLng latLng = null;
            for (DocumentSnapshot documentSnapshot: querySnapshot.getDocuments()) {
                try {
                    GeoPoint geoPoint = (GeoPoint) documentSnapshot.getData().get("location");
                    if (geoPoint != null) {
                        User user = documentSnapshot.toObject(User.class);

                        latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        Marker marker = googleMap.addMarker(
                                new MarkerOptions()
                                .position(latLng)
                                .title(user.getName())
                                .snippet(user.getEmail())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account))
                        );
                        marker.setTag(user);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // animate for last location
            if (latLng != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }
}
