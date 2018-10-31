package me.varunon9.saathmetravel.ui.chat;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import me.varunon9.saathmetravel.ChatFragmentActivity;
import me.varunon9.saathmetravel.R;
import me.varunon9.saathmetravel.adapters.ChatListRecyclerViewAdapter;
import me.varunon9.saathmetravel.constants.AppConstants;
import me.varunon9.saathmetravel.models.Chat;
import me.varunon9.saathmetravel.utils.FirestoreDbOperationCallback;
import me.varunon9.saathmetravel.utils.FirestoreQuery;
import me.varunon9.saathmetravel.utils.FirestoreQueryConditionCode;

public class ChatListFragment extends Fragment {

    private ViewModel chatViewModel;
    private RecyclerView recyclerView;
    private ChatFragmentActivity chatFragmentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        chatFragmentActivity = (ChatFragmentActivity) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatViewModel = ViewModelProviders.of(this.getActivity()).get(ChatViewModel.class);

        getChatListFromFirestore();

    }

    private void getChatListFromFirestore() {
        final List<Chat> chatList = new ArrayList<>();

        chatFragmentActivity.showProgressDialog("Fetching chats", "Please wait", false);
        List<FirestoreQuery> firestoreQueryList = new ArrayList<>();
        firestoreQueryList.add(new FirestoreQuery(
                FirestoreQueryConditionCode.WHERE_ARRAY_CONTAINS,
                "participantsUid",
                chatFragmentActivity.userUid
        ));

        chatFragmentActivity.firestoreDbUtility.getMany(AppConstants.Collections.CHATS,
                firestoreQueryList, new FirestoreDbOperationCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        QuerySnapshot querySnapshot = (QuerySnapshot) object;
                        for (DocumentSnapshot documentSnapshot: querySnapshot.getDocuments()) {
                            Chat chat = documentSnapshot.toObject(Chat.class);
                            chatList.add(chat);
                            recyclerView.setAdapter(new ChatListRecyclerViewAdapter(chatList,
                                    chatViewModel, chatFragmentActivity));
                            chatFragmentActivity.dismissProgressDialog();
                        }
                        if (chatList.isEmpty()) {
                            chatFragmentActivity.showMessage(
                                    "No chats found. Please initiate one from traveller's profile"
                            );
                        }
                        chatFragmentActivity.dismissProgressDialog();
                    }

                    @Override
                    public void onFailure(Object object) {
                        chatFragmentActivity.dismissProgressDialog();
                        chatFragmentActivity.showMessage("Failed to fetch chat list.");
                    }
                });
    }
}
