package me.varunon9.saathmetravel.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.varunon9.saathmetravel.PlaceDetailActivity;
import me.varunon9.saathmetravel.R;
import me.varunon9.saathmetravel.models.PlaceReview;

public class PlaceReviewListRecyclerViewAdapter
        extends RecyclerView.Adapter<PlaceReviewListRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceReview> placeReviewList;
    private PlaceDetailActivity placeDetailActivity;

    public PlaceReviewListRecyclerViewAdapter(List<PlaceReview> placeReviewList,
                                              PlaceDetailActivity placeDetailActivity) {
        this.placeDetailActivity = placeDetailActivity;
        this.placeReviewList = placeReviewList;
    }

    @NonNull
    @Override
    public PlaceReviewListRecyclerViewAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_review_fragment_item, parent, false);
        return new PlaceReviewListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mItem = placeReviewList.get(position);
        holder.reviewerNameTextView.setText(holder.mItem.getReviewerName());
        holder.ratingTextView.setText(String.valueOf(holder.mItem.getRating()) + " / 5");
        holder.reviewTextView.setText(holder.mItem.getReview());
        holder.updatedAtTextView.setText(
                placeDetailActivity.generalUtility.convertDateToChatDateFormat(holder.mItem.getUpdatedAt())
        );
    }

    @Override
    public int getItemCount() {
        return placeReviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView reviewerNameTextView;
        final TextView reviewTextView;
        final TextView ratingTextView;
        final TextView updatedAtTextView;
        PlaceReview mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            reviewerNameTextView = (TextView) view.findViewById(R.id.reviewerNameTextView);
            reviewTextView = (TextView) view.findViewById(R.id.reviewTextView);
            ratingTextView = (TextView) view.findViewById(R.id.ratingTextView);
            updatedAtTextView = (TextView) view.findViewById(R.id.updatedAtTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + reviewerNameTextView.getText() + "'";
        }
    }
}
