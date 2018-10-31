package me.varunon9.saathmetravel.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    private String id;
    private String senderUid;
    private String receiverUid;

    private @ServerTimestamp Date date;

    public Message() {
        date = new Date();
    }
}
