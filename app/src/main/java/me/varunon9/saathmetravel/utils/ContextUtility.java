package me.varunon9.saathmetravel.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import me.varunon9.saathmetravel.constants.AppConstants;

/**
 * Created by varunkumar on 30/6/18.
 *
 * This Utility class needs context
 */

public class ContextUtility {
    private Context context;
    private String TAG = "ContextUtility";

    public ContextUtility(Context context) {
        this.context = context;
    }

    public boolean isPermissionGranted(String permission) {
        int result = context.checkCallingOrSelfPermission(permission);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isBuildVersionGreaterEqualToMarshmallow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }
        return false;
    }

    /**
     * zoom level range-
     *
     * 1: World
     * 5: Landmass/continent
     * 10: City
     * 15: Streets
     * 20: Buildings
     */
    public void showLocationOnMap(GoogleMap googleMap, Location location,
                                  String marker, boolean moveCamera, float zoomLevel) {
        // default location: Bangalore
        LatLng currentLocation = AppConstants.DEFAULT_LAT_LNG;

        if (location != null) {
            currentLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }
        googleMap.addMarker(new MarkerOptions().position(currentLocation)
                .title(marker));
        if (moveCamera) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    currentLocation, zoomLevel)
            );
        }
    }

    public boolean isConnectedToNetwork() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void populateSimpleListView(ListView listView, ArrayList<String> list) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                list
        );
        listView.setAdapter(arrayAdapter);
    }

    public void shareApp() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = AppConstants.SHARE_APP_BODY;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                AppConstants.SHARE_APP_SUBJECT);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                | Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + context.getPackageName())));
        }
    }

    public void storeFcmTokenInSharedPreference(String token) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(AppConstants.FCM_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstants.FCM_TOKEN, token);
        editor.apply();
        Log.d(TAG, "FCM token saved: " + token);
    }

    public String getFcmTokenFromSharedPreference() {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(AppConstants.FCM_TOKEN, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(AppConstants.FCM_TOKEN, null);
        return token;
    }
}
