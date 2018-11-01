package me.varunon9.saathmetravel.ui.chat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import me.varunon9.saathmetravel.models.User;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> selectedTraveller = new MutableLiveData<>();

    public MutableLiveData<User> getSelectedTraveller() {
        return selectedTraveller;
    }

    public void setSelectedTraveller(User user) {
        selectedTraveller.setValue(user);
    }
}
