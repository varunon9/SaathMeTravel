package me.varunon9.saathmetravel.utils.map;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.varunon9.saathmetravel.R;
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
                                         LatLng sourceLatLng, LatLng destinationLatLng) {
        if (googleMap == null) {
            return;
        }
        Log.d(TAG, "drawPathBetweenTwoLatLng called");

        googleMap.addMarker(new MarkerOptions().position(sourceLatLng)
                .title("source")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_time_to_leave)));

        googleMap.addMarker(new MarkerOptions().position(destinationLatLng)
                .title("destination")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination)));

        String directionApiUrl = getDirectionApiUrl(sourceLatLng, destinationLatLng);
        ajaxUtility.makeHttpRequest(directionApiUrl, "GET", null,
                new AjaxCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        System.out.println(response);
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
        String sensor = "sensor=false";
        String parameters = originString
                + "&" + destinationString;
        String url = AppConstants.Urls.DIRECTIONS + "?" + parameters;
        Log.d(TAG, url);
        return url;
    }
}
