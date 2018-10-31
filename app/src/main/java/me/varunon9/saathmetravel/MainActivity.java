package me.varunon9.saathmetravel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
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

        View mapView = findViewById(R.id.map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        contextUtility = new ContextUtility(this);
        generalUtility = new GeneralUtility();
        firestoreDbUtility = new FirestoreDbUtility();

        // check if internet connection is available
        if (!contextUtility.isConnectedToNetwork()) {
            Snackbar.make(mapView, AppConstants.INTERNET_CONNECTION_IS_MANDATORY, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        checkLoginAndUpdateUi(navigationView);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                // todo: set isOnline false, update lastSeen if user is loggedIn
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
        getMenuInflater().inflate(R.menu.main, menu);
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
        } else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady called");
        mMap = googleMap;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mMap.setOnMarkerClickListener(this);
        if (bundle != null) {
        }
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
                true, 12);
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
                    mMap.clear(); // clear initial marker
                    contextUtility.showLocationOnMap(mMap, location, AppConstants.CURRENT_LOCATION_MARKER,
                            true, 12);
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
            MenuItem loginMenuItem = navigationDrawerMenu.findItem(R.id.nav_login);
            MenuItem logoutMenuItem = navigationDrawerMenu.findItem(R.id.nav_logout);

            if (firebaseUser == null) {
                // user is not logged in
                profileMenuItem.setVisible(false);
                logoutMenuItem.setVisible(false);
            } else {
                String displayName = firebaseUser.getDisplayName();
                String email = firebaseUser.getEmail();
                Uri photoUrl = firebaseUser.getPhotoUrl();
                String phoneNumber = firebaseUser.getPhoneNumber();

                loginMenuItem.setVisible(false);

                if (displayName != null) {
                    navigationHeaderTitleTextView.setText(displayName);
                } else if (phoneNumber != null) {
                    navigationHeaderTitleTextView.setText(phoneNumber);
                } else if (email != null) {
                    navigationHeaderTitleTextView.setText(email);
                }

                if (email != null) {
                    navigationHeaderSubTitleTextView.setText(email);
                }

                // todo: set profile pic when loggedIn
                navigationHeaderImageView.setImageResource(R.mipmap.ic_account);

                // set isOnline true and update user's location
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("online", true);
                firestoreDbUtility.update(AppConstants.Collections.USERS,
                        firebaseUser.getUid(), hashMap, new FirestoreDbOperationCallback() {
                            @Override
                            public void onSuccess(Object object) {
                            }

                            @Override
                            public void onFailure(Object object) {
                            }
                        });
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
            if (location != null) {
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("location", new GeoPoint(location.getLatitude(), location.getLongitude()));
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
        }
        if (singleton.getSourcePlace() != null && singleton.getDestinationPlace() != null) {
            showFellowTravellersOnMap(singleton);
        } else {
            showNearbyTravellersOnMap(location);
        }
    }

    private void showNearbyTravellersOnMap(Location location) {
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
                firestoreQueryList, new FirestoreDbOperationCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        QuerySnapshot querySnapshot = (QuerySnapshot) object;
                        generalUtility.showTravellersOnMap(mMap, querySnapshot);
                    }

                    @Override
                    public void onFailure(Object object) {
                        showMessage("Failed to locate nearby travellers.");
                    }
                });
    }

    private void showFellowTravellersOnMap(Singleton singleton) {
        LatLng sourceLatLng = singleton.getSourcePlace().getLatLng();
        LatLng destinationLatLng = singleton.getDestinationPlace().getLatLng();
        int filterRange = singleton.getFilterRange();

        GeoPoint sourceLocationLesserGeoPoint =
                generalUtility.getLesserGeoPointFromLatLng(sourceLatLng, filterRange);
        GeoPoint sourceLocationGreaterGeoPoint =
                generalUtility.getGreaterGeoPointFromLatLng(sourceLatLng, filterRange);
        GeoPoint destinationLocationLesserGeoPoint =
                generalUtility.getLesserGeoPointFromLatLng(destinationLatLng, filterRange);
        GeoPoint destinationLocationGreaterGeoPoint =
                generalUtility.getGreaterGeoPointFromLatLng(destinationLatLng, filterRange);

        List<FirestoreQuery> firestoreQueryList = new ArrayList<>();
        firestoreQueryList.add(new FirestoreQuery(
                FirestoreQueryConditionCode.WHERE_LESS_THAN,
                "sourceLocation",
                sourceLocationGreaterGeoPoint
        ));
        firestoreQueryList.add(new FirestoreQuery(
                FirestoreQueryConditionCode.WHERE_GREATER_THAN,
                "sourceLocation",
                sourceLocationLesserGeoPoint
        ));
        // todo: use destinationFilter as well
        /*firestoreQueryList.add(new FirestoreQuery(
                FirestoreQueryConditionCode.WHERE_LESS_THAN,
                "destinationLocation",
                destinationLocationGreaterGeoPoint
        ));
        firestoreQueryList.add(new FirestoreQuery(
                FirestoreQueryConditionCode.WHERE_GREATER_THAN,
                "destinationLocation",
                destinationLocationLesserGeoPoint
        ));*/

        firestoreDbUtility.getMany(AppConstants.Collections.SEARCH_HISTORIES,
                firestoreQueryList, new FirestoreDbOperationCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        QuerySnapshot querySnapshot = (QuerySnapshot) object;
                        Set<String> userUidSet = new HashSet<>();
                        for (DocumentSnapshot documentSnapshot: querySnapshot) {
                            userUidSet.add(documentSnapshot.getData().get("userUid").toString());
                        }
                        if (userUidSet.isEmpty()) {
                            showMessage("No Fellow travellers found. Plan different travel");
                        } else {
                            if (mMap != null) {
                                mMap.clear();
                                for (String userUid: userUidSet) {
                                    generalUtility.showSingleTravellerOnMap(firestoreDbUtility,
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

    private void showMessage(String message) {
        View parentLayout = findViewById(R.id.activityContent);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, marker.getTitle() + " clicked");
        try {
            User user = (User) marker.getTag();
            if (singleton.getFirebaseUser() == null) {
                showMessage("You need to login to chat with traveller");
            } else {
                // todo: go to chat Activity
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
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        singleton.setFirebaseUser(null);

                        // todo: update isOnline and lastSeen
                        refreshMainActivity();
                    }
                });
    }

    private void refreshMainActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }
}
