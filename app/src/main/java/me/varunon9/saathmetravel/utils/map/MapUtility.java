package me.varunon9.saathmetravel.utils.map;

import android.content.Context;
import android.location.Location;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.varunon9.saathmetravel.R;
import me.varunon9.saathmetravel.Singleton;
import me.varunon9.saathmetravel.constants.AppConstants;
import me.varunon9.saathmetravel.utils.ajax.AjaxCallback;
import me.varunon9.saathmetravel.utils.ajax.AjaxUtility;

public class MapUtility {

    private Context context;
    private AjaxUtility ajaxUtility;
    private String TAG = "MapUtility";

    public MapUtility(Context context) {
        this.context = context;
        ajaxUtility = new AjaxUtility(context.getApplicationContext());
    }

    public void drawPathBetweenTwoLatLng(GoogleMap googleMap,
                                         Singleton singleton) {

        LatLng sourceLatLng =  singleton.getSourcePlace().getLatLng();
        LatLng destinationLatLng = singleton.getDestinationPlace().getLatLng();
        CameraPosition cameraPosition = singleton.getGoogleMapCurrentCameraPosition();

        // if cameraPosition is not null, that means path has already been drawn
        if (googleMap == null || cameraPosition != null) {
            return;
        }

        int journeyDistance = (int) getDistanceBetweenLatLng(sourceLatLng, destinationLatLng);

        googleMap.addMarker(new MarkerOptions().position(sourceLatLng)
                .title(singleton.getSourcePlace().getName().toString())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_time_to_leave)))
                .setTag(singleton.getSourcePlace());

        googleMap.addMarker(new MarkerOptions().position(destinationLatLng)
                .title(singleton.getDestinationPlace().getName().toString())
                .snippet(journeyDistance + " KM far from " + singleton.getSourcePlace().getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination)))
                .setTag(singleton.getDestinationPlace());

        int zoomLevel = getZoomLevelBasedOnSourceAndDestination(journeyDistance);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng, zoomLevel));

        String directionApiUrl = getDirectionApiUrl(sourceLatLng, destinationLatLng);
        ajaxUtility.makeHttpRequest(directionApiUrl, "GET", null,
                new AjaxCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        RouteDrawerTask routeDrawerTask = new RouteDrawerTask(googleMap, context);
                        try {
                            routeDrawerTask.execute(response.getJSONObject("result"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(JSONObject response) {
                        Log.e(TAG, response.toString());
                    }
                });

    }

    private String getDirectionApiUrl(LatLng sourceLatLng, LatLng destinationLatLng) {
        String originString = "origin="
                + sourceLatLng.latitude + "," + sourceLatLng.longitude;
        String destinationString = "destination="
                + destinationLatLng.latitude + "," + destinationLatLng.longitude;
        String parameters = originString
                + "&" + destinationString;
        String url = AppConstants.Urls.DIRECTIONS + "?" + parameters;
        Log.d(TAG, url);
        return url;
    }

    private int getZoomLevelBasedOnSourceAndDestination(float distance) {
        int zoomLevel = 5;
        if (distance < 2) {
            zoomLevel = 12;
        } else if (distance < 8) {
            zoomLevel = 11;
        } else if (distance < 16) {
            zoomLevel = 10;
        } else if (distance < 32) {
            zoomLevel = 9;
        } else if (distance < 128) {
            zoomLevel = 8;
        } else if (distance < 256) {
            zoomLevel = 7;
        } else if (distance < 512) {
            zoomLevel = 6;
        }

        Log.d(TAG, String.valueOf(zoomLevel));

        return zoomLevel;
    }

    private float getDistanceBetweenLatLng(LatLng sourceLatLng, LatLng destinationLatLng) {
        Location sourceLocation = new Location("source");
        sourceLocation.setLatitude(sourceLatLng.latitude);
        sourceLocation.setLongitude(sourceLatLng.longitude);

        Location destinationLocation = new Location("destination");
        destinationLocation.setLatitude(destinationLatLng.latitude);
        destinationLocation.setLongitude(destinationLatLng.longitude);

        return (sourceLocation.distanceTo(destinationLocation) / 1000); // KM
    }
}
