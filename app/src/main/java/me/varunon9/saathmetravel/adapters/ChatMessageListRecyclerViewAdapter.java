package me.varunon9.saathmetravel.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.varunon9.saathmetravel.ChatFragmentActivity;
import me.varunon9.saathmetravel.R;
import me.varunon9.saathmetravel.models.Message;

public class ChatMessageListRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<Message> messageList;
    private String loggedinUserUid;
    private ChatFragmentActivity chatFragmentActivity;

    public ChatMessageListRecyclerViewAdapter(List<Message> messageList,
                                              String loggedinUserUid,
                                              ChatFragmentActivity chatFragmentActivity) {
        this.messageList = messageList;
        this.loggedinUserUid = loggedinUserUid;
        this.chatFragmentActivity = chatFragmentActivity;
    }

    // Inflates the appropriate layout according to the ViewType.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_sent_item, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_received_item, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) messageList.get(position);
        if (message.getInitiatorUid().equals(loggedinUserUid)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageBodyTextView, messageTimeTextView;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageBodyTextView = (TextView) itemView.findViewById(R.id.messageBodyTextView);
            messageTimeTextView = (TextView) itemView.findViewById(R.id.messageTimeTextView);
        }

        void bind(Message message) {
            messageBodyTextView.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            messageTimeTextView.setText(chatFragmentActivity.generalUtility
                    .convertDateToChatDateFormat(message.getCreatedAt())
            );
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageBodyTextView, messageTimeTextView;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageBodyTextView = (TextView) itemView.findViewById(R.id.messageBodyTextView);
            messageTimeTextView = (TextView) itemView.findViewById(R.id.messageTimeTextView);
        }

        void bind(Message message) {
            messageBodyTextView.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            messageTimeTextView.setText(chatFragmentActivity.generalUtility
                    .convertDateToChatDateFormat(message.getCreatedAt())
            );
        }
    }
}
