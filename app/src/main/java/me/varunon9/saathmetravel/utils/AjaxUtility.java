package me.varunon9.saathmetravel.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.varunon9.saathmetravel.Singleton;
import me.varunon9.saathmetravel.constants.AppConstants;

/**
 * Created by varunkumar on 24/7/18.
 */

public class AjaxUtility {
    private static final String LOG = "AjaxUtility";
    Singleton singleton;
    Context context;

    public AjaxUtility(Context context) {
        this.context = context;
        singleton = Singleton.getInstance(context);
    }

    public void makeHttpRequest(String url, String method,
                                JSONObject body, final AjaxCallback ajaxCallback) {
        int methodCode = Request.Method.GET;
        if (method.toLowerCase().equals("post")) {
            methodCode = Request.Method.POST;
        } else if (method.toLowerCase().equals("put")) {
            methodCode = Request.Method.PUT;
        } else if (method.toLowerCase().equals("delete")) {
            methodCode = Request.Method.DELETE;
        }
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(methodCode,
                    url, body, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(LOG, response.toString());
                    ajaxCallback.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    JSONObject responseObject = new JSONObject();
                    try {
                        NetworkResponse response = error.networkResponse;
                        JSONObject errorObject = new JSONObject(new String(response.data));
                        if (response != null) {
                            int statusCode = response.statusCode;
                            responseObject.put("statusCode", statusCode);
                            responseObject.put("message", errorObject.get("message"));
                            if (errorObject.has("errors")) {
                                responseObject.put("errors", errorObject.get("errors"));
                            } else {
                                responseObject.put("errors", null);
                            }
                        } else {
                            responseObject.put("statusCode", 0); // it shouldn't happen
                            responseObject.put("message", AppConstants.GENERIC_ERROR_MESSAGE);
                            responseObject.put("errors", null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ajaxCallback.onError(responseObject);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String>  params = new HashMap<>();
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return params;
                }
            };
            singleton.getRequestQueue().add(jsonObjectRequest);
        } catch(Exception e) {
            Log.d(LOG, "Exception makeHttpRequest");
            e.printStackTrace();
        }
    }
}
