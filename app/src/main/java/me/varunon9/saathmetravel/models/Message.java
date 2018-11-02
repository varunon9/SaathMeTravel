package me.varunon9.saathmetravel.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    private String initiatorUid;
    private String recipientUid;
    private String message;

    private @ServerTimestamp Date createdAt;

    public Message() {
        createdAt = new Date();
    }

    public String getInitiatorUid() {
        return initiatorUid;
    }

    public void setInitiatorUid(String initiatorUid) {
        this.initiatorUid = initiatorUid;
    }

    public String getRecipientUid() {
        return recipientUid;
    }

    public void setRecipientUid(String recipientUid) {
        this.recipientUid = recipientUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
