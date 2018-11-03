package me.varunon9.saathmetravel.ui.chat;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import me.varunon9.saathmetravel.ChatFragmentActivity;
import me.varunon9.saathmetravel.R;
import me.varunon9.saathmetravel.adapters.ChatMessageListRecyclerViewAdapter;
import me.varunon9.saathmetravel.constants.AppConstants;
import me.varunon9.saathmetravel.models.Chat;
import me.varunon9.saathmetravel.models.Message;
import me.varunon9.saathmetravel.models.User;
import me.varunon9.saathmetravel.utils.FirestoreDbOperationCallback;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;
    private ChatFragmentActivity chatFragmentActivity;
    private ChatMessageListRecyclerViewAdapter chatMessageListRecyclerViewAdapter;
    private Button chatBoxSendButton;
    private EditText chatBoxEditText;
    private String conversationUrl;
    private String TAG = "ChatFragment";
    private Chat currentChat;
    private List<Message> messageList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_fragment, container, false);
        chatFragmentActivity = (ChatFragmentActivity) getActivity();

        RecyclerView chatMessageListRecyclerView =
                rootView.findViewById(R.id.chatMessageListRecyclerView);
        chatMessageListRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        chatMessageListRecyclerViewAdapter = new ChatMessageListRecyclerViewAdapter(
                messageList, chatFragmentActivity.chatInitiatorUid
        );
        chatMessageListRecyclerView.setAdapter(chatMessageListRecyclerViewAdapter);

        chatBoxSendButton = rootView.findViewById(R.id.chatBoxSendButton);
        chatBoxEditText = rootView.findViewById(R.id.chatBoxEditText);
        chatBoxSendButton.setOnClickListener((view) -> {
            sendMessage();
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        chatViewModel.getSelectedChat().observe(this, chat -> {
            currentChat = chat;
            chatFragmentActivity.updateActionBarTitle(chat.getRecipientName());
            getRecipientProfileFromFirestore(chat.getRecipientUid());
            conversationUrl = AppConstants.Collections.CHAT_MESSAGES
                    + "/"
                    + chat.getId()
                    + "/messages";
            getChatMessages(conversationUrl);
        });
    }

    private void getRecipientProfileFromFirestore(String recipientUid) {
        chatFragmentActivity.showProgressDialog("Fetching Recipient info",
                "Please wait", false);
        chatFragmentActivity.firestoreDbUtility.getOne(AppConstants.Collections.USERS,
                recipientUid, new FirestoreDbOperationCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        DocumentSnapshot documentSnapshot = (DocumentSnapshot) object;
                        User recipientUser = documentSnapshot.toObject(User.class);
                        chatFragmentActivity.dismissProgressDialog();

                        updateLastSeen(recipientUser);
                    }

                    @Override
                    public void onFailure(Object object) {
                        chatFragmentActivity.dismissProgressDialog();
                        chatFragmentActivity.showMessage("Failed to fetch recipient info");
                    }
                });
    }

    private void updateLastSeen(User recipientUser) {
        // todo: update last seen
    }

    private void sendMessage() {
        String message = chatBoxEditText.getText().toString();
        if (message != null) {
            chatBoxEditText.setText("");
            Message messageToBeSent = new Message();
            messageToBeSent.setMessage(message);
            messageToBeSent.setInitiatorUid(currentChat.getInitiatorUid());
            messageToBeSent.setRecipientUid(currentChat.getRecipientUid());

            String documentName = chatFragmentActivity.generalUtility
                    .getUniqueDocumentId(chatFragmentActivity.chatInitiatorUid);
            chatFragmentActivity.firestoreDbUtility.createOrMerge(conversationUrl, documentName,
                    messageToBeSent, new FirestoreDbOperationCallback() {
                        @Override
                        public void onSuccess(Object object) {
                            Log.i(TAG, message + " sent");
                        }

                        @Override
                        public void onFailure(Object object) {
                            Log.e(TAG, "message '" + message + "' not sent");
                        }
                    });
        }
    }

    private void getChatMessages(String conversationUrl) {
        chatFragmentActivity.firestoreDbUtility.getMany(conversationUrl, null,
                new FirestoreDbOperationCallback() {
            @Override
            public void onSuccess(Object object) {
                QuerySnapshot querySnapshot = (QuerySnapshot) object;
                for (DocumentSnapshot documentSnapshot: querySnapshot.getDocuments()) {
                    Message message = documentSnapshot.toObject(Message.class);
                    messageList.add(message);
                }
                chatMessageListRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Object object) {
                chatFragmentActivity.showMessage("Failed to fetch messages");
            }
        });
    }

}
