package me.varunon9.saathmetravel.constants;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by varunkumar on 1/7/18.
 */

public final class AppConstants {
    // can't instantiate
    private AppConstants() {
    }

    public static final int ACCESS_LOCATION_REQUEST_CODE = 1;
    public static final int LOGIN_REQUEST_CODE = 2;
    public static final String ACCESS_LOCATION_TOAST_MESSAGE =
            "Please grant location permission to use this service";
    public static final String CURRENT_LOCATION_MARKER = "You are here";
    public static final String INTERNET_CONNECTION_IS_MANDATORY =
            "Internet connection is mandatory.";
    public static final String GENERIC_ERROR_MESSAGE = "Something went wrong";

    public static final String SHARE_APP_SUBJECT = "SaathMeTravel";
    public static final String SHARE_APP_BODY =
            "A social travelling app to match the travellers sharing a common journey."
            + "\nDownload the app now"
            + "\nhttps://play.google.com/store/apps/details?id=me.varunon9.saathmetravel";
    public static final String USER_DEFAULT_PREFERENCE = "I like mostly trekking and sightseeing"
            + " and expecting similar preference from fellow travellers.";

    public static class Gender {
        private Gender() {}

        public static final String MALE = "male";
        public static final String FEMALE = "female";
    }

    public static class Collections {
        private Collections() {}

        public static final String USERS = "users";
        public static final String SEARCH_HISTORIES = "searchHistories";
    }

    // default/fallback location would be Bangalore
    public static final LatLng DEFAULT_LAT_LNG = new LatLng(12.97, 77.6);
    public static final int DEFAULT_RANGE = 50; // 50 KM

}