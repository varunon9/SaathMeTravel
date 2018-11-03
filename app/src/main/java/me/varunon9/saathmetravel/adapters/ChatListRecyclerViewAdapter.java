package me.varunon9.saathmetravel.adapters;

import android.arch.lifecycle.ViewModel;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.varunon9.saathmetravel.ChatFragmentActivity;
import me.varunon9.saathmetravel.R;
import me.varunon9.saathmetravel.models.Chat;
import me.varunon9.saathmetravel.ui.chat.ChatViewModel;

import java.util.List;

public class ChatListRecyclerViewAdapter extends
        RecyclerView.Adapter<ChatListRecyclerViewAdapter.ViewHolder> {

    private final List<Chat> mValues;
    private ChatViewModel chatViewModel;
    private ChatFragmentActivity chatFragmentActivity;

    public ChatListRecyclerViewAdapter(List<Chat> items, ViewModel chatViewModel,
                                       ChatFragmentActivity chatFragmentActivity) {
        mValues = items;
        this.chatViewModel = (ChatViewModel) chatViewModel;
        this.chatFragmentActivity = chatFragmentActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if (mValues.get(position).getInitiatorUid().equals(chatFragmentActivity.chatInitiatorUid)) {
            // display recipient name
            holder.recipientTextView.setText(mValues.get(position).getRecipientName());
        } else {
            // display initiator name
            holder.recipientTextView.setText(mValues.get(position).getInitiatorName());
        }

        holder.lastMessageTextView.setText(mValues.get(position).getLastMessage());
        holder.timeTextView.setText(chatFragmentActivity.generalUtility
                .convertDateToChatDateFormat(mValues.get(position).getUpdatedAt())
        );

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatViewModel.setSelectedChat(holder.mItem);
                chatFragmentActivity.goToChatFragment();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView recipientTextView;
        public final TextView lastMessageTextView;
        public final TextView timeTextView;
        public Chat mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            recipientTextView = (TextView) view.findViewById(R.id.recipientTextView);
            lastMessageTextView = (TextView) view.findViewById(R.id.lastMessageTextView);
            timeTextView = (TextView) view.findViewById(R.id.timeTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + recipientTextView.getText() + "'";
        }
    }
}
