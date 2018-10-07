package me.varunon9.saathmetravel;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This class contains global variables
 */

public class Singleton {
    private static Singleton singleton;
    private RequestQueue requestQueue;
    private Context context;
    private LocationManager locationManager;
    private String TAG = "Singleton";
    private FirebaseUser firebaseUser;
    private boolean checkUserLogin = true;
    private Place sourcePlace;
    private Place destinationPlace;
    private int filterRange;

    private Singleton(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
        filterRange = 5; // 5 KM by default
    }

    public static synchronized Singleton getInstance(Context context) {
        if (singleton == null) {
            singleton = new Singleton(context);
        }
        return singleton;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public Location getCurrentLocation()
            throws SecurityException {
        if (locationManager == null) {
            locationManager =
                    (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        }
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(
                locationManager.getBestProvider(criteria, false)
        );
        return location;
    }

    public FirebaseUser getFirebaseUser() {
        if (firebaseUser == null && checkUserLogin) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            checkUserLogin = false; // only one time

            // todo: save user location
        }
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;

        // todo: save user location
    }

    public Place getSourcePlace() {
        return sourcePlace;
    }

    public void setSourcePlace(Place sourcePlace) {
        this.sourcePlace = sourcePlace;
    }

    public Place getDestinationPlace() {
        return destinationPlace;
    }

    public void setDestinationPlace(Place destinationPlace) {
        this.destinationPlace = destinationPlace;
    }

    public int getFilterRange() {
        return filterRange;
    }

    public void setFilterRange(int filterRange) {
        this.filterRange = filterRange;
    }
}
