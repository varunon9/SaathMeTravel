package me.varunon9.saathmetravel.constants;

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

    public static final String shareAppSubject = "SaathMeTravel";
    public static final String shareAppBody =
            "A social travelling app to match the travellers sharing a common journey."
            + "\nDownload the app now"
            + "\nhttps://play.google.com/store/apps/details?id=me.varunon9.saathmetravel";
    public static final String userDefaultPreference = "I like mostly trekking and sightseeing"
            + " and expecting similar preference from fellow travellers.";

}