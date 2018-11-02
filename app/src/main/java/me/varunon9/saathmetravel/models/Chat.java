package me.varunon9.saathmetravel.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chat {

    private String id;
    private String initiatorUid;
    private String initiatorName;
    private String recipientUid;
    private String recipientName;
    private String lastMessage;

    private List<String> participantsUid;

    private @ServerTimestamp Date createdAt;
    private @ServerTimestamp Date updatedAt;

    public Chat() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public List<String> getParticipantsUid() {
        return participantsUid;
    }

    public void setParticipantsUid(List<String> participantsUid) {
        this.participantsUid = participantsUid;
    }
}
