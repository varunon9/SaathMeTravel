package me.varunon9.saathmetravel.ui.place;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.varunon9.saathmetravel.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPlaceReviewFragment extends Fragment {


    public AddPlaceReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_place_review, container, false);
    }

}
