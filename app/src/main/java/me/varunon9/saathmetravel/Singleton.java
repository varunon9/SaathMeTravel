package me.varunon9.saathmetravel;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by varunkumar on 02/9/18.
 */

public class Singleton {
    private static Singleton singleton;
    private RequestQueue requestQueue;
    private Context context;
    private LocationManager locationManager;
    private String TAG = "Singleton";

    private Singleton(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
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
}
