package me.varunon9.saathmetravel.utils.map;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.varunon9.saathmetravel.R;

/**
 * Courtesy: https://github.com/ar-android/DrawRouteMaps
 */
public class RouteDrawerTask extends AsyncTask<JSONObject, Integer,
        List<List<HashMap<String, String>>>> {

    private GoogleMap googleMap;
    private String TAG = "RouteDrawerTask";
    private PolylineOptions lineOptions;
    private int routeColor;
    private Context context;

    public RouteDrawerTask(GoogleMap googleMap, Context context) {
        this.googleMap = googleMap;
        this.context = context;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(JSONObject... jsonObjects) {
        JSONObject directionsResponse = jsonObjects[0];
        List<List<HashMap<String, String>>> routes = null;

        try {
            RouteDataParser routeDataParser = new RouteDataParser();
            routes = routeDataParser.parse(directionsResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        if (result != null) {
            drawPolyLine(result);
        }
    }

    private void drawPolyLine(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        lineOptions = null;

        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(6);
            routeColor = ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, null);
            if (routeColor == 0) {
                lineOptions.color(0xFF0A8F08);
            }
            else {
                lineOptions.color(routeColor);
            }
        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null && googleMap != null) {
            googleMap.addPolyline(lineOptions);
        } else {
            Log.d(TAG, "without Polylines draw");
        }
    }
}
