package me.varunon9.saathmetravel.utils.ajax;

import org.json.JSONObject;

/**
 * Created by varunkumar on 24/7/18.
 */

public interface AjaxCallback {
    void onSuccess(JSONObject response);
    void onError(JSONObject response);
}
