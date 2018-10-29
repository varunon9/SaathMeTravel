package me.varunon9.saathmetravel.arrayAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.varunon9.saathmetravel.R;
import me.varunon9.saathmetravel.models.SearchHistory;

public class SearchHistoryArrayAdapter extends ArrayAdapter<SearchHistory> {

    private Context context;
    private int layoutResourceId;
    private List<SearchHistory> searchHistoryList;

    public SearchHistoryArrayAdapter(@NonNull Context context, int layoutResourceId,
                                     List<SearchHistory> searchHistoryList) {
        super(context, layoutResourceId, searchHistoryList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.searchHistoryList = searchHistoryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchHistoryItemHolder searchHistoryItemHolder = null;
        SearchHistory searchHistory = searchHistoryList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutResourceId, parent, false);
            searchHistoryItemHolder = new SearchHistoryItemHolder();
            searchHistoryItemHolder.sourceAddressTextView =
                    convertView.findViewById(R.id.sourceAddress);
            searchHistoryItemHolder.destinationAddressTextView =
                    convertView.findViewById(R.id.destinationAddress);
            convertView.setTag(searchHistoryItemHolder);
        } else {
            searchHistoryItemHolder = (SearchHistoryItemHolder) convertView.getTag();
        }

        searchHistoryItemHolder.sourceAddressTextView.setText(searchHistory.getSourceAddress());
        searchHistoryItemHolder.destinationAddressTextView.setText(searchHistory.getDestinationAddress());

        return convertView;
    }

    static class SearchHistoryItemHolder {
        TextView sourceAddressTextView;
        TextView destinationAddressTextView;
    }
}
