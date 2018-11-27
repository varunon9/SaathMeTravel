package me.varunon9.saathmetravel.ui.place;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import me.varunon9.saathmetravel.PlaceDetailActivity;
import me.varunon9.saathmetravel.R;
import me.varunon9.saathmetravel.constants.AppConstants;
import me.varunon9.saathmetravel.models.PlaceReview;
import me.varunon9.saathmetravel.models.User;
import me.varunon9.saathmetravel.utils.FirestoreDbOperationCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPlaceReviewFragment extends Fragment {

    private PlaceDetailActivity placeDetailActivity;
    private RatingBar placeRatingBar;
    private EditText placeReviewEditText;
    private Button addOrUpdateReviewButton;
    private PlaceReview placeReview;
    private String placeReviewId;
    private String TAG = "AddPlaceReviewFragment";

    public AddPlaceReviewFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_place_review, container, false);
        placeDetailActivity = (PlaceDetailActivity) getActivity();

        placeRatingBar = rootView.findViewById(R.id.placeRatingBar);
        placeReviewEditText = rootView.findViewById(R.id.placeReviewEditText);
        addOrUpdateReviewButton = rootView.findViewById(R.id.addOrUpdateReviewButton);

        getPlaceReview();

        return rootView;
    }

    private void getPlaceReview() {
        User loggedInUser = placeDetailActivity.singleton.getLoggedInUser();
        if (loggedInUser != null) {
            placeReviewId = loggedInUser.getUid()
                    + "_"
                    + placeDetailActivity.selectedPlace.getId();
        } else {
            Toast.makeText(placeDetailActivity, "You need to login to submit a review",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        placeDetailActivity.firestoreDbUtility.getOne(AppConstants.Collections.PLACE_REVIEWS,
                placeReviewId, new FirestoreDbOperationCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;
                        placeReview = documentSnapshot.toObject(PlaceReview.class);

                        // user might update his name at a later point of time
                        if (loggedInUser.getName() != null && !loggedInUser.getName().isEmpty()) {
                            placeReview.setReviewerName(loggedInUser.getName());
                        } else {
                            placeReview.setReviewerName("No Name");
                        }

                        setUserReview(placeReview);
                        setRatingAndButtonListener();
                    }

                    @Override
                    public void onFailure(Object object) {
                        Log.d(TAG, "Creating new Review");

                        // User has not submitted any review, lets create one
                        placeReview = new PlaceReview();
                        placeReview.setId(placeReviewId);
                        placeReview.setPlaceId(placeDetailActivity.selectedPlace.getId());
                        placeReview.setUserUid(loggedInUser.getUid());

                        if (loggedInUser.getName() != null && !loggedInUser.getName().isEmpty()) {
                            placeReview.setReviewerName(loggedInUser.getName());
                        } else {
                            placeReview.setReviewerName("No Name");
                        }

                        setRatingAndButtonListener();
                    }
                });

    }

    private void setUserReview(PlaceReview placeReview) {
        placeRatingBar.setRating(placeReview.getRating());
        placeReviewEditText.setText(placeReview.getReview());
    }

    private void setRatingAndButtonListener() {
        placeRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                placeReview.setRating(rating);
            }
        });

        addOrUpdateReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrUpdateReview();
            }
        });
    }

    private void addOrUpdateReview() {
        if (placeReview.getRating() <= 0) {
            placeDetailActivity.showMessage("Please give your rating out of 5");
            return;
        }
        String review = placeReviewEditText.getText().toString();
        if (review.isEmpty()) {
            placeDetailActivity.showMessage("Please type your review");
            return;
        }
        placeReview.setReview(review);

        placeDetailActivity.showProgressDialog("Submitting Review", "Please wait", false);
        placeDetailActivity.firestoreDbUtility.createOrMerge(AppConstants.Collections.PLACE_REVIEWS,
                placeReview.getId(), placeReview, new FirestoreDbOperationCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        placeDetailActivity.dismissProgressDialog();
                        placeDetailActivity.showMessage("Successfully submitted review");
                    }

                    @Override
                    public void onFailure(Object object) {
                        placeDetailActivity.dismissProgressDialog();
                        placeDetailActivity.showMessage("Failed to submit review");
                    }
                });
    }

}
