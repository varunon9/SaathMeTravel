package me.varunon9.saathmetravel.ui.chat;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;

import me.varunon9.saathmetravel.ChatFragmentActivity;
import me.varunon9.saathmetravel.R;
import me.varunon9.saathmetravel.constants.AppConstants;
import me.varunon9.saathmetravel.models.Chat;
import me.varunon9.saathmetravel.models.User;
import me.varunon9.saathmetravel.utils.FirestoreDbOperationCallback;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;
    private ChatFragmentActivity chatFragmentActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_fragment, container, false);
        chatFragmentActivity = (ChatFragmentActivity) getActivity();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        chatViewModel.getSelectedChat().observe(this, chat -> {
            chatFragmentActivity.updateActionBarTitle(chat.getRecipientName());
            getRecipientProfileFromFirestore(chat.getRecipientUid());
            // todo: update messages
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

}
