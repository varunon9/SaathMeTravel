package me.varunon9.saathmetravel.ui.place;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.varunon9.saathmetravel.PlaceDetailActivity;
import me.varunon9.saathmetravel.R;
import me.varunon9.saathmetravel.adapters.PlaceReviewListRecyclerViewAdapter;
import me.varunon9.saathmetravel.constants.AppConstants;
import me.varunon9.saathmetravel.models.PlaceReview;
import me.varunon9.saathmetravel.utils.FirestoreDbOperationCallback;
import me.varunon9.saathmetravel.utils.FirestoreQuery;
import me.varunon9.saathmetravel.utils.FirestoreQueryConditionCode;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceReviewsFragment extends Fragment {

    private PlaceDetailActivity placeDetailActivity;
    private List<PlaceReview> placeReviewList;
    private RecyclerView recyclerView;
    private PlaceReviewListRecyclerViewAdapter placeReviewListRecyclerViewAdapter;

    public PlaceReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place_reviews, container, false);
        placeDetailActivity = (PlaceDetailActivity) getActivity();
        
        if (rootView instanceof RecyclerView) {
            Context context = rootView.getContext();
            recyclerView = (RecyclerView) rootView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            placeReviewList = new ArrayList<>();

        }
        
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        placeReviewListRecyclerViewAdapter = new PlaceReviewListRecyclerViewAdapter(placeReviewList,
                placeDetailActivity);
        recyclerView.setAdapter(placeReviewListRecyclerViewAdapter);

        getPlaceReviewListFromFirestore();

    }

    private void getPlaceReviewListFromFirestore() {

        placeDetailActivity.showProgressDialog("Fetching reviews", "Please wait", false);
        List<FirestoreQuery> firestoreQueryList = new ArrayList<>();
        firestoreQueryList.add(new FirestoreQuery(
                FirestoreQueryConditionCode.WHERE_EQUAL_TO,
                "placeId",
                placeDetailActivity.selectedPlace.getId()
        ));
        Map<String, Object> orderbyHashMap = new HashMap<>();
        orderbyHashMap.put("updatedAt", Query.Direction.DESCENDING);

        // todo: put limit 200
        placeDetailActivity.firestoreDbUtility.getMany(AppConstants.Collections.PLACE_REVIEWS,
                firestoreQueryList, orderbyHashMap, new FirestoreDbOperationCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        QuerySnapshot querySnapshot = (QuerySnapshot) object;
                        for (DocumentSnapshot documentSnapshot: querySnapshot.getDocuments()) {
                            PlaceReview placeReview = documentSnapshot.toObject(PlaceReview.class);
                            placeReviewList.add(placeReview);
                        }
                        placeReviewListRecyclerViewAdapter.notifyDataSetChanged();

                        if (placeReviewList.isEmpty()) {
                            placeDetailActivity.showMessage(
                                    "No reviews found"
                            );
                        }
                        placeDetailActivity.dismissProgressDialog();
                    }

                    @Override
                    public void onFailure(Object object) {
                        placeDetailActivity.dismissProgressDialog();
                        placeDetailActivity.showMessage("Failed to fetch reviews");
                    }
                });
    }

}
