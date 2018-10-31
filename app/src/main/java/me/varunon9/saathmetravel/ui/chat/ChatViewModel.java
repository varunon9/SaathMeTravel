package me.varunon9.saathmetravel.ui.chat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import me.varunon9.saathmetravel.models.Chat;

public class ChatViewModel extends ViewModel {

    private final MutableLiveData<Chat> selectedChat = new MutableLiveData<>();

    public MutableLiveData<Chat> getSelectedChat() {
        return selectedChat;
    }

    public void setSelectedChat(Chat chat) {
        selectedChat.setValue(chat);
    }
}
