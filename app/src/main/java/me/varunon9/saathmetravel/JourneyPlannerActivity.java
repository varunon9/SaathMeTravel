package me.varunon9.saathmetravel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.varunon9.saathmetravel.adapters.SearchHistoryArrayAdapter;
import me.varunon9.saathmetravel.constants.AppConstants;
import me.varunon9.saathmetravel.models.SearchHistory;
import me.varunon9.saathmetravel.utils.FirestoreDbOperationCallback;
import me.varunon9.saathmetravel.utils.FirestoreDbUtility;
import me.varunon9.saathmetravel.utils.FirestoreQuery;
import me.varunon9.saathmetravel.utils.FirestoreQueryConditionCode;
import me.varunon9.saathmetravel.utils.GeneralUtility;

import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_ADDRESS;

public class JourneyPlannerActivity extends AppCompatActivity {

    private String TAG = "JourneyPlannerActivity";
    private Singleton singleton;
    private ProgressDialog progressDialog;
    private FirestoreDbUtility firestoreDbUtility;
    private GeneralUtility generalUtility;
    private PlaceAutocompleteFragment sourceAutocompleteFragment;
    private PlaceAutocompleteFragment destinationAutocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_planner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        singleton = Singleton.getInstance(getApplicationContext());
        firestoreDbUtility = new FirestoreDbUtility();
        generalUtility = new GeneralUtility();

        sourceAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.source_autocomplete_fragment);
        destinationAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.destination_autocomplete_fragment);

        // setting filter
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                //.setTypeFilter(TYPE_FILTER_ADDRESS)
                .build();
        sourceAutocompleteFragment.setFilter(autocompleteFilter);
        destinationAutocompleteFragment.setFilter(autocompleteFilter);

        // setting hint or selected places
        setSelectedSourceAndDestinationPlace(sourceAutocompleteFragment,
                destinationAutocompleteFragment);

        // adding clear button listener
        sourceAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sourceAutocompleteFragment.setText("");
                        singleton.setSourcePlace(null);
                    }
                });
        destinationAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        destinationAutocompleteFragment.setText("");
                        singleton.setDestinationPlace(null);
                    }
                });

        // changing icon
        ImageView sourceSearchIcon = (ImageView)((LinearLayout)sourceAutocompleteFragment
                .getView()).getChildAt(0);
        sourceSearchIcon.setImageResource(R.drawable.ic_time_to_leave);

        ImageView destinationSearchIcon = (ImageView)((LinearLayout)destinationAutocompleteFragment
                .getView()).getChildAt(0);
        destinationSearchIcon.setImageResource(R.drawable.ic_destination);


        sourceAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                String placeAddress = place.getAddress().toString();
                Log.i(TAG, "Place: " + placeAddress);
                singleton.setSourcePlace(place);
            }

            @Override
            public void onError(Status status) {
                Log.e(TAG, "An error occurred: " + status);
                showMessage(AppConstants.GENERIC_ERROR_MESSAGE + " Status: " + status);
            }
        });
        destinationAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                String placeAddress = place.getAddress().toString();
                Log.i(TAG, "Place: " + placeAddress);
                singleton.setDestinationPlace(place);
            }

            @Override
            public void onError(Status status) {
                Log.e(TAG, "An error occurred: " + status);
                showMessage(AppConstants.GENERIC_ERROR_MESSAGE + " Status: " + status);
            }
        });

        // populating history listView if loggedin
        if (singleton.getFirebaseUser() != null) {
            populateSearchHistoryListView(singleton);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void showMessage(String message) {
        View parentLayout = findViewById(R.id.activityContent);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void showProgressDialog(String title, String message, boolean isCancellable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(JourneyPlannerActivity.this);
        }
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(isCancellable);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void onRangeRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.range10RadioButton: {
                if (checked) {
                    singleton.setFilterRange(10);
                }
                break;
            }
            case R.id.range20RadioButton: {
                if (checked) {
                    singleton.setFilterRange(20);
                }
                break;
            }
            case R.id.range50RadioButton: {
                if (checked) {
                    singleton.setFilterRange(50);
                }
                break;
            }
        }
    }

    public void onSearchTravellersButtonClicked(View view) {
        Place sourcePlace = singleton.getSourcePlace();
        Place destinationPlace = singleton.getDestinationPlace();

        if (sourcePlace == null || destinationPlace == null) {
            showMessage("Please enter source as well as destination");
            return;
        }
        if (singleton.getFirebaseUser() != null) {
            saveSearchHistory(sourcePlace, destinationPlace, singleton);
        }

        // clear current zoom level and target
        singleton.setGoogleMapCurrentCameraPosition(null);

        // go to MainActivity
        Intent intent = new Intent(JourneyPlannerActivity.this, MainActivity.class);

        // clear history stack so that back button does no lead to JourneyPlannerActivity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setSelectedSourceAndDestinationPlace(
            PlaceAutocompleteFragment sourceAutocompleteFragment,
            PlaceAutocompleteFragment destinationAutocompleteFragment) {

        Place sourcePlace = singleton.getSourcePlace();
        Place destinationPlace = singleton.getDestinationPlace();

        if (sourcePlace != null) {
            sourceAutocompleteFragment.setText(sourcePlace.getAddress().toString());
        } else {
            sourceAutocompleteFragment.setHint(
                    getResources().getString(R.string.enter_source_hint_text)
            );
        }

        if (destinationPlace != null) {
            destinationAutocompleteFragment.setText(destinationPlace.getAddress().toString());
        } else {
            destinationAutocompleteFragment.setHint(
                    getResources().getString(R.string.enter_destination_hint_text)
            );
        }
    }

    private void populateSearchHistoryListView(Singleton singleton) {
        final ListView searchHistoryListView = findViewById(R.id.searchHistoryListView);
        final List<SearchHistory> searchHistoryList = new ArrayList<>();

        // getting last 24 hours search histories from firestore
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR,-1);
        Date yesterdaysDate = calendar.getTime();

        List<FirestoreQuery> firestoreQueryList = new ArrayList<>();
        firestoreQueryList.add(new FirestoreQuery(
                FirestoreQueryConditionCode.WHERE_GREATER_THAN,
                "createdAt",
                yesterdaysDate
        ));
        firestoreQueryList.add(new FirestoreQuery(
                FirestoreQueryConditionCode.WHERE_EQUAL_TO,
                "userUid",
                singleton.getFirebaseUser().getUid()
        ));

        Map<String, Object> orderbyHashMap = new HashMap<>();
        orderbyHashMap.put("createdAt", Query.Direction.DESCENDING);

        firestoreDbUtility.getMany(AppConstants.Collections.SEARCH_HISTORIES,
                firestoreQueryList, orderbyHashMap, new FirestoreDbOperationCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        try {
                            QuerySnapshot querySnapshot = (QuerySnapshot) object;
                            for (DocumentSnapshot documentSnapshot: querySnapshot.getDocuments()) {
                                searchHistoryList.add(documentSnapshot.toObject(SearchHistory.class));
                            }
                            searchHistoryListView.setAdapter(new SearchHistoryArrayAdapter(
                                    getBaseContext(),
                                    R.layout.search_history_list_item,
                                    searchHistoryList)
                            );

                            // hide last 24 hours info text when there is no history
                            TextView searchHistoryListInfoTextView =
                                    (TextView) findViewById(R.id.searchHistoryListInfoTextView);
                            if (searchHistoryList.isEmpty()) {
                                searchHistoryListInfoTextView.setVisibility(View.INVISIBLE);
                            } else {
                                searchHistoryListInfoTextView.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Object object) {
                        showMessage("Failed to fetch last 24 hours search histories.");
                    }
                });

        // on click of ListView would set source and destination to Singleton
        searchHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchHistory searchHistory = searchHistoryList.get(position);
                if (searchHistory.getSourceAddress() != null
                        && searchHistory.getSourceLocation() != null) {
                    Place sourcePlace =
                            generalUtility.getPlaceFromSearchHistory(searchHistory, true);
                    singleton.setSourcePlace(sourcePlace);
                    sourceAutocompleteFragment.setText(sourcePlace.getAddress());
                }
                if (searchHistory.getDestinationAddress() != null
                        && searchHistory.getDestinationLocation() != null) {
                    Place destinationPlace =
                            generalUtility.getPlaceFromSearchHistory(searchHistory, false);
                    singleton.setDestinationPlace(destinationPlace);
                    destinationAutocompleteFragment.setText(destinationPlace.getAddress());
                }
                onSearchTravellersButtonClicked(null);
            }
        });

    }

    private void saveSearchHistory(Place sourcePlace, Place destinationPlace, Singleton singleton) {
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setSourceAddress(sourcePlace.getAddress().toString());
        searchHistory.setDestinationAddress(destinationPlace.getAddress().toString());
        searchHistory.setSourcePlaceId(sourcePlace.getId());
        searchHistory.setDestinationPlaceId(destinationPlace.getId());

        FirebaseUser firebaseUser = singleton.getFirebaseUser();
        searchHistory.setUserUid(firebaseUser.getUid());

        GeoPoint sourceLocation = new GeoPoint(sourcePlace.getLatLng().latitude,
                sourcePlace.getLatLng().longitude);
        GeoPoint destinationLocation = new GeoPoint(destinationPlace.getLatLng().latitude,
                destinationPlace.getLatLng().longitude);
        searchHistory.setSourceLocation(sourceLocation);
        searchHistory.setDestinationLocation(destinationLocation);

        final String id = generalUtility.getUniqueDocumentId(firebaseUser.getUid());
        searchHistory.setId(id);

        firestoreDbUtility.createOrMerge(AppConstants.Collections.SEARCH_HISTORIES,
                id, searchHistory, new FirestoreDbOperationCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        Log.d(TAG, "Search history saved " + id);
                    }

                    @Override
                    public void onFailure(Object object) {
                        Log.e(TAG, "Failed to save search history " + id);
                    }
                });
    }
}
