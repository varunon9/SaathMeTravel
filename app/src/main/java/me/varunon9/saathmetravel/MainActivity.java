package me.varunon9.saathmetravel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.varunon9.saathmetravel.constants.AppConstants;
import me.varunon9.saathmetravel.models.User;
import me.varunon9.saathmetravel.utils.ContextUtility;
import me.varunon9.saathmetravel.utils.FirestoreDbOperationCallback;
import me.varunon9.saathmetravel.utils.FirestoreDbUtility;
import me.varunon9.saathmetravel.utils.FirestoreQuery;
import me.varunon9.saathmetravel.utils.FirestoreQueryConditionCode;
import me.varunon9.saathmetravel.utils.GeneralUtility;
import me.varunon9.saathmetravel.utils.map.MapUtility;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ContextUtility contextUtility;
    private static final String TAG = "MainActivity";
    private Singleton singleton;
    private boolean doubleBackToExitPressedOnce = false;
    private GeneralUtility generalUtility;
    private FirestoreDbUtility firestoreDbUtility;
    private MapUtility mapUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        singleton = Singleton.getInstance(getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        contextUtility = new ContextUtility(this);
        generalUtility = new GeneralUtility();
        firestoreDbUtility = new FirestoreDbUtility();
        mapUtility = new MapUtility(this);

        // check if internet connection is available
        if (!contextUtility.isConnectedToNetwork()) {
            showMessage(AppConstants.INTERNET_CONNECTION_IS_MANDATORY);
        }
        checkLoginAndUpdateUi(navigationView);

        checkAndShowUpdateAvailableAlert();

        // todo: check getIntent().getExtras().keySet() for notification data and take actions
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            doubleBackToExitPressedOnce = true;
            showMessage("Please click BACK again to exit");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_share) {
            contextUtility.shareApp();
        } else if (id == R.id.nav_rate) {
            contextUtility.rateApp();
        } else if (id == R.id.nav_login) {
            firebaseLogin();
        } else if (id == R.id.nav_logout) {
            firebaseLogout();
        } else if (id == R.id.nav_profile) {
            Bundle args = new Bundle();
            args.putInt(AppConstants.NAVIGATION_ITEM, id);
            args.putString(AppConstants.CHAT_RECIPIENT_UID, singleton.getFirebaseUser().getUid());
            goToChatFragmentActivity(args);
        } else if (id == R.id.nav_chats) {
            Bundle args = new Bundle();
            args.putInt(AppConstants.NAVIGATION_ITEM, id);
            goToChatFragmentActivity(args);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady called");
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        Location location = null;
        if (contextUtility.isBuildVersionGreaterEqualToMarshmallow()) {
            if (contextUtility.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // show the user his current location
                location = singleton.getCurrentLocation();
            } else {
                // request for location permission
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, AppConstants.ACCESS_LOCATION_REQUEST_CODE);
            }
        } else {
            // show the user his current location
            location = singleton.getCurrentLocation();
        }

        contextUtility.showLocationOnMap(mMap, location, AppConstants.CURRENT_LOCATION_MARKER,
                true, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.LOGIN_REQUEST_CODE: {
                IdpResponse response = IdpResponse.fromResultIntent(data);

                if (resultCode == RESULT_OK) {
                    // Successfully signed in
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    Log.i(TAG,firebaseUser.getDisplayName()
                            + ", " + firebaseUser.getEmail()
                            + ", " + firebaseUser.getPhoneNumber() + ", " + firebaseUser.toString());
                    singleton.setFirebaseUser(firebaseUser);

                    // create user if not already created
                    User user = generalUtility.convertFirebaseUserToUser(firebaseUser,
                            singleton.getCurrentLocation());
                    user.setFcmToken(contextUtility.getFcmTokenFromSharedPreference());
                    firestoreDbUtility.createOrMerge(AppConstants.Collections.USERS,
                            user.getUid(), user, new FirestoreDbOperationCallback() {
                                @Override
                                public void onSuccess(Object object) {
                                }

                                @Override
                                public void onFailure(Object object) {
                                }
                            });
                    refreshMainActivity();
                } else {
                    // Sign in failed. If response is null the user canceled the
                    // sign-in flow using the back button. Otherwise check
                    // response.getError().getErrorCode() and handle the error.
                    if (response != null) {
                        int errorCode = response.getError().getErrorCode();
                        showMessage(errorCode + ": " + AppConstants.GENERIC_ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case AppConstants.ACCESS_LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, show user his current location
                    Location location = singleton.getCurrentLocation();
                    //mMap.clear(); // clear initial marker
                    contextUtility.showLocationOnMap(mMap, location, AppConstants.CURRENT_LOCATION_MARKER,
                            true, 10);
                } else {
                    // permission denied, show user toast notification
                    Toast.makeText(this, AppConstants.ACCESS_LOCATION_TOAST_MESSAGE,
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void checkLoginAndUpdateUi(NavigationView navigationView ) {
        FirebaseUser firebaseUser = singleton.getFirebaseUser();
        try {
            View navigationDrawerHeaderLayout = navigationView.getHeaderView(0);
            Menu navigationDrawerMenu = navigationView.getMenu();

            ImageView navigationHeaderImageView = navigationDrawerHeaderLayout
                    .findViewById(R.id.navigationHeaderImageView);
            TextView navigationHeaderTitleTextView = navigationDrawerHeaderLayout
                    .findViewById(R.id.navigationHeaderTitleTextView);
            TextView navigationHeaderSubTitleTextView = navigationDrawerHeaderLayout
                    .findViewById(R.id.navigationHeaderSubTitleTextView);

            MenuItem profileMenuItem = navigationDrawerMenu.findItem(R.id.nav_profile);
            MenuItem chatsMenuItem = navigationDrawerMenu.findItem(R.id.nav_chats);
            MenuItem loginMenuItem = navigationDrawerMenu.findItem(R.id.nav_login);
            MenuItem logoutMenuItem = navigationDrawerMenu.findItem(R.id.nav_logout);

            if (firebaseUser == null) {
                // user is not logged in
                profileMenuItem.setVisible(false);
                chatsMenuItem.setVisible(false);
                logoutMenuItem.setVisible(false);
            } else {
                String displayName = firebaseUser.getDisplayName();
                String email = firebaseUser.getEmail();
                Uri photoUrl = firebaseUser.getPhotoUrl();
                String phoneNumber = firebaseUser.getPhoneNumber();

                loginMenuItem.setVisible(false);

                Log.d(TAG, displayName + ", " + email + ", " + phoneNumber);
                if (displayName != null && !displayName.isEmpty()) {
                    navigationHeaderTitleTextView.setText(displayName);
                } else if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    navigationHeaderTitleTextView.setText(phoneNumber);
                } else if (email != null && !email.isEmpty()) {
                    navigationHeaderTitleTextView.setText(email);
                }

                if (email != null && !email.isEmpty()) {
                    navigationHeaderSubTitleTextView.setText(email);
                }

                // todo: set profile pic when loggedIn
                navigationHeaderImageView.setImageResource(R.mipmap.ic_account);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        FirebaseUser firebaseUser = singleton.getFirebaseUser();
        Location location = singleton.getCurrentLocation();
        if (firebaseUser != null) {
            Map<String, Object> hashMap = new HashMap<>();

            if (location != null) {
                hashMap.put("location", new GeoPoint(location.getLatitude(), location.getLongitude()));
            }

            hashMap.put("online", true);
            hashMap.put("lastSeen", new Date());
            firestoreDbUtility.update(AppConstants.Collections.USERS, firebaseUser.getUid(),
                    hashMap, new FirestoreDbOperationCallback() {

                        @Override
                        public void onSuccess(Object object) {
                        }

                        @Override
                        public void onFailure(Object object) {
                        }
                    });
        }
        if (singleton.getSourcePlace() != null && singleton.getDestinationPlace() != null) {
            showFellowTravellersOnMap(singleton);
        } else {
            showNearbyTravellersOnMap(location);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        if (mMap != null) {
            mMap.clear(); // clearing google map when leaving activity
        }

        // updating last seen and isOnline = false
        if (singleton.getFirebaseUser() != null) {
            generalUtility.setUserLastSeenStatus(firestoreDbUtility,
                    singleton.getFirebaseUser().getUid());
        }
    }

    private void showNearbyTravellersOnMap(Location location) {
        Log.d(TAG, "showNearbyTravellersOnMap for " + location);
        if (location == null) {
            // this should only happen when permission is not granted
            showMessage("Unable to get your location. Please grant location permission from device settings");
            return;
        }
        int range = AppConstants.DEFAULT_RANGE;
        GeoPoint lesserGeoPoint = generalUtility.getLesserGeoPointFromLocation(location, range);
        GeoPoint greaterGeoPoint = generalUtility.getGreaterGeoPointFromLocation(location, range);

        List<FirestoreQuery> firestoreQueryList = new ArrayList<>();
        firestoreQueryList.add(new FirestoreQuery(
                FirestoreQueryConditionCode.WHERE_LESS_THAN,
                "location",
                greaterGeoPoint
        ));
        firestoreQueryList.add(new FirestoreQuery(
                FirestoreQueryConditionCode.WHERE_GREATER_THAN,
                "location",
                lesserGeoPoint
        ));
        firestoreDbUtility.getMany(AppConstants.Collections.USERS,
                firestoreQueryList,null,  new FirestoreDbOperationCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        QuerySnapshot querySnapshot = (QuerySnapshot) object;
                        generalUtility.showTravellersOnMap(mMap, querySnapshot);
                        if (querySnapshot.isEmpty()) {
                            showMessage("No nearby travellers found.");
                        } else {
                            showMessage(querySnapshot.size() + " travellers found nearby");
                        }
                    }

                    @Override
                    public void onFailure(Object object) {
                        showMessage("Failed to locate nearby travellers.");
                    }
                });
    }

    private void showFellowTravellersOnMap(Singleton singleton) {
        Log.d(TAG, "showFellowTravellersOnMap called");
        LatLng sourceLatLng = singleton.getSourcePlace().getLatLng();
        LatLng destinationLatLng = singleton.getDestinationPlace().getLatLng();
        int filterRange = singleton.getFilterRange();

        try {
            generalUtility.getTravellersAroundALocation(sourceLatLng, filterRange,
                    firestoreDbUtility, singleton, true,
                    new FirestoreDbOperationCallback() {
                        @Override
                        public void onSuccess(Object object) {
                            Set<String> sourceUserUidSet = (Set<String>) object;
                            generalUtility.getTravellersAroundALocation(destinationLatLng, filterRange,
                                    firestoreDbUtility, singleton, false,
                                    new FirestoreDbOperationCallback() {
                                        @Override
                                        public void onSuccess(Object object) {
                                            Set<String> destinationUserUidSet = (Set<String>) object;

                                            // merging the userUidSet
                                            Set<String> userUidSet = new HashSet<>();
                                            for (String userUid: sourceUserUidSet) {
                                                if (destinationUserUidSet.contains(userUid)) {
                                                    userUidSet.add(userUid);
                                                }
                                            }

                                            mapUtility.drawPathBetweenTwoLatLng(mMap, sourceLatLng, destinationLatLng);

                                            if (userUidSet.isEmpty()) {
                                                showMessage("No Fellow travellers found. Plan different travel");
                                            } else {
                                                showMessage(userUidSet.size() + " fellow travellers found. Zoom out map to see all.");
                                                if (mMap != null) {
                                                    //mMap.clear();
                                                    for (String userUid: userUidSet) {
                                                        generalUtility.showSingleTravellerOnMap(
                                                                firestoreDbUtility,
                                                                mMap, userUid);
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Object object) {
                                            showMessage("Failed to locate fellow travellers.");
                                        }
                                    });
                        }

                        @Override
                        public void onFailure(Object object) {
                            showMessage("Failed to locate fellow travellers.");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Something went wrong. Clear the journey filters to get nearby travellers");
        }
    }

    private void showMessage(String message) {
        View parentLayout = findViewById(R.id.activityContent);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, marker.getTitle() + " clicked");
        try {
            User user = (User) marker.getTag();
            if (user != null) {
                if (singleton.getFirebaseUser() == null) {
                    showMessage("You need to login to chat with traveller");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.CHAT_RECIPIENT_UID, user.getUid());
                    bundle.putInt(AppConstants.NAVIGATION_ITEM, R.id.nav_profile);
                    goToChatFragmentActivity(bundle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // calling from XML hence public
    public void goToJourneyPlannerActivity(View view) {
        Intent intent = new Intent(MainActivity.this, JourneyPlannerActivity.class);
        startActivity(intent);
    }

    private void firebaseLogin() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                        .setLogo(R.mipmap.ic_launcher)
                        .setTheme(R.style.AppTheme)
                        .build(),
                AppConstants.LOGIN_REQUEST_CODE);
    }

    private void firebaseLogout() {
        // updating isOnline = false and lastSeen
        generalUtility.setUserLastSeenStatus(firestoreDbUtility,
                singleton.getFirebaseUser().getUid());

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {

                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... voids) {
                                try {
                                    FirebaseInstanceId.getInstance().deleteInstanceId();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                singleton.setFirebaseUser(null);
                                refreshMainActivity();
                            }
                        }.execute();
                    }
                });
    }

    private void refreshMainActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    private void goToChatFragmentActivity(Bundle bundle) {
        bundle.putString(AppConstants.CHAT_INITIATOR_UID, singleton.getFirebaseUser().getUid());
        bundle.putString(AppConstants.CHAT_INITIATOR_NAME, singleton.getFirebaseUser().getDisplayName());
        Intent intent = new Intent(MainActivity.this, ChatFragmentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void checkAndShowUpdateAvailableAlert() {
        try {
            String VERSION = "version";
            String NEW_FEATURES = "newFeatures";

            if (singleton.isUpdateAvailable()) {
                FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(BuildConfig.DEBUG)
                        .build();
                firebaseRemoteConfig.setConfigSettings(configSettings);

                Map<String, Object> defaultValueHashMap = new HashMap<>();
                defaultValueHashMap.put(VERSION, BuildConfig.VERSION_CODE);
                defaultValueHashMap.put(NEW_FEATURES, "");

                firebaseRemoteConfig.setDefaults(defaultValueHashMap);

                long cacheExpiration = 3600; // 1 hour in seconds.
                if (firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
                    cacheExpiration = 0;
                }

                firebaseRemoteConfig.fetch(cacheExpiration)
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // showing update alert only one time
                                    singleton.setUpdateAvailable(false);

                                    firebaseRemoteConfig.activateFetched();
                                    long remoteVersionCode = firebaseRemoteConfig.getLong(VERSION);
                                    String newFeatures = firebaseRemoteConfig.getString(NEW_FEATURES);
                                    Log.d(TAG, "Remote version: " + remoteVersionCode
                                            + ", New Features: " + newFeatures);
                                    if (remoteVersionCode > BuildConfig.VERSION_CODE
                                            && newFeatures != null
                                            && !newFeatures.isEmpty()) {
                                        contextUtility.showUpdateAlert(newFeatures);
                                    }

                                } else {
                                    Log.e(TAG, "Remote config fetch failed");
                                }
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
