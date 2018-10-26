package me.varunon9.saathmetravel;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import me.varunon9.saathmetravel.constants.AppConstants;

import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_ADDRESS;

public class JourneyPlannerActivity extends AppCompatActivity {

    private String TAG = "JourneyPlannerActivity";
    private Singleton singleton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_planner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        singleton = Singleton.getInstance(getApplicationContext());

        PlaceAutocompleteFragment sourceAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.source_autocomplete_fragment);
        PlaceAutocompleteFragment destinationAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.destination_autocomplete_fragment);

        // setting filter
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(TYPE_FILTER_ADDRESS)
                .build();
        sourceAutocompleteFragment.setFilter(autocompleteFilter);
        destinationAutocompleteFragment.setFilter(autocompleteFilter);

        // setting hint or selected places
        setSelectedSourceAndDestinationPlace(sourceAutocompleteFragment,
                destinationAutocompleteFragment);

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
            case R.id.range5RadioButton: {
                if (checked) {
                    singleton.setFilterRange(5);
                }
                break;
            }
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
        }
    }

    public void onSearchTravellersButtonClicked(View view) {
        Place sourcePlace = singleton.getSourcePlace();
        Place destinationPlace = singleton.getDestinationPlace();
        int range = singleton.getFilterRange();

        if (sourcePlace == null) {

        }
        // todo: get fellow travellers data and pass to MainActivity via Bundle
        // todo: show user `No Travellers Found` message
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
}
