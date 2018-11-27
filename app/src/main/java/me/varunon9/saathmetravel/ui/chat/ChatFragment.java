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

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ListenerRegistration listenerRegistration;
    private RecyclerView chatMessageListRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_fragment, container, false);
        chatFragmentActivity = (ChatFragmentActivity) getActivity();

        chatMessageListRecyclerView =
                rootView.findViewById(R.id.chatMessageListRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rootView.getContext());
        linearLayoutManager.setStackFromEnd(true);
        chatMessageListRecyclerView.setLayoutManager(linearLayoutManager);
        chatMessageListRecyclerViewAdapter = new ChatMessageListRecyclerViewAdapter(
                messageList, chatFragmentActivity.chatInitiatorUid, chatFragmentActivity
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
            if (chat.getInitiatorUid().equals(chatFragmentActivity.chatInitiatorUid)) {
                getRecipientProfileFromFirestore(chat.getRecipientUid());
            } else {
                getRecipientProfileFromFirestore(chat.getInitiatorUid());
            }
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
        if (recipientUser == null) {
            // deleted from firestore db?
            return;
        }
        // update online or last seen
        String title = recipientUser.getName();
        if (title == null) {
            title = recipientUser.getEmail();
        }
        if (title == null) {
            title = recipientUser.getMobile();
        }
        String subtitle = "last seen at ";
        if (recipientUser.isOnline()) {
            subtitle = "online";
        } else {
            subtitle += chatFragmentActivity.generalUtility
                    .convertDateToChatDateFormat(recipientUser.getLastSeen());
        }
        chatFragmentActivity.updateActionBarTitle(title, subtitle);
    }

    // send message as update lastMessage
    private void sendMessage() {
        String message = chatBoxEditText.getText().toString();
        if (message != null) {
            chatBoxEditText.setText("");
            Message messageToBeSent = new Message();
            messageToBeSent.setMessage(message);
            messageToBeSent.setInitiatorUid(chatFragmentActivity.chatInitiatorUid);

            if (chatFragmentActivity.chatInitiatorUid.equals(currentChat.getInitiatorUid())) {
                messageToBeSent.setRecipientUid(currentChat.getRecipientUid());
            } else {
                messageToBeSent.setRecipientUid(currentChat.getInitiatorUid());
            }

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

            // silently update lastMessage
            Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("lastMessage", message);
            chatFragmentActivity.firestoreDbUtility.update(AppConstants.Collections.CHATS,
                    currentChat.getId(), hashMap, new FirestoreDbOperationCallback() {
                        @Override
                        public void onSuccess(Object object) {
                            Log.i(TAG, message + " lastMessage updated");
                        }

                        @Override
                        public void onFailure(Object object) {
                            Log.e(TAG, "message '" + message + "' lastMessage not updated");
                        }
                    });
        }
    }

    private void getChatMessages(String conversationUrl) {
        // getting last 200 messages
        Query query = chatFragmentActivity.firestoreDbUtility.getDb().collection(conversationUrl)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .limit(200);
        listenerRegistration = query.addSnapshotListener(
                (queryDocumentSnapshots, e) -> {

                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED: {
                                Message message = dc.getDocument().toObject(Message.class);
                                Log.d(TAG, "New message: " + message.getMessage());
                                messageList.add(message);
                                chatMessageListRecyclerViewAdapter.notifyDataSetChanged();
                                chatMessageListRecyclerView.scrollToPosition(messageList.size() - 1);
                                break;
                            }
                        }
                    }

                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

}
