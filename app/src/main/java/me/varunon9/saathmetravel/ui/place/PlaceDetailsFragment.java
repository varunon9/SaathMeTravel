package me.varunon9.saathmetravel.ui.place;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.URLEncoder;

import me.varunon9.saathmetravel.PlaceDetailActivity;
import me.varunon9.saathmetravel.R;
import me.varunon9.saathmetravel.constants.AppConstants;
import me.varunon9.saathmetravel.utils.ajax.AjaxCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceDetailsFragment extends Fragment {

    public PlaceDetailActivity placeDetailActivity;

    private TextView placeDescriptionTextView;
    private TextView placeAddressTextView;
    private TextView placeRatingTextView;
    private TextView placeWebsiteTextView;
    private TextView placePhoneTextView;


    public PlaceDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place_details, container, false);
        placeDetailActivity = (PlaceDetailActivity) getActivity();

        placeDescriptionTextView = rootView.findViewById(R.id.placeDescriptionTextView);
        placeAddressTextView = rootView.findViewById(R.id.placeAddressTextView);
        placeRatingTextView = rootView.findViewById(R.id.placeRatingTextView);
        placeWebsiteTextView = rootView.findViewById(R.id.placeWebsiteTextView);
        placePhoneTextView = rootView.findViewById(R.id.placePhoneTextView);

        if (placeDetailActivity.selectedPlaceExtract != null) {
            placeDescriptionTextView.setText(placeDetailActivity.selectedPlaceExtract);
        } else {
            showWikipediaDescriptionOfPlace();
        }

        placeAddressTextView.setText(placeDetailActivity.selectedPlace.getAddress());

        if (placeDetailActivity.selectedPlace.getRating() > 0) {
            placeRatingTextView.setText(String.valueOf(placeDetailActivity.selectedPlace.getRating()));
        } else {
            placeRatingTextView.setText("NA");
        }

        if (placeDetailActivity.selectedPlace.getWebsiteUri() != null
                && !placeDetailActivity.selectedPlace.getWebsiteUri().toString().isEmpty()) {
            placeWebsiteTextView.setText(placeDetailActivity.selectedPlace.getWebsiteUri().toString());
        } else {
            placeWebsiteTextView.setText("NA");
        }

        if (placeDetailActivity.selectedPlace.getPhoneNumber() != null
                && !placeDetailActivity.selectedPlace.getPhoneNumber().toString().isEmpty()) {
            placePhoneTextView.setText(placeDetailActivity.selectedPlace.getPhoneNumber().toString());
        } else {
            placePhoneTextView.setText("NA");
        }

        return rootView;
    }

    private void showWikipediaDescriptionOfPlace() {
        String url = null;
        try {
            url = AppConstants.Urls.PLACE_SUMMARY
                    + URLEncoder.encode(placeDetailActivity.selectedPlace.getName().toString(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (url == null) {
            return; // encoding exception
        }

        placeDetailActivity.ajaxUtility.makeHttpRequest(url, "GET", null, new AjaxCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONObject pagesJsonObject = response.getJSONObject("query").getJSONObject("pages");
                    String pageId = pagesJsonObject.keys().next();
                    String extract = pagesJsonObject.getJSONObject(pageId).getString("extract");
                    placeDetailActivity.selectedPlaceExtract = extract;
                    placeDescriptionTextView.setText(extract);
                } catch (Exception e) {
                    e.printStackTrace();
                    // to fill the gap and prevent further request
                    placeDetailActivity.selectedPlaceExtract = placeDetailActivity
                            .selectedPlace.getName().toString();
                    placeDescriptionTextView.setText(placeDetailActivity.selectedPlaceExtract);
                }
            }

            @Override
            public void onError(JSONObject response) {

            }
        });
    }

}
