package me.varunon9.saathmetravel.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class SearchHistory {

    private String id;
    private String userUid;
    private GeoPoint sourceLocation;
    private GeoPoint destinationLocation;
    private String sourceAddress;
    private String destinationAddress;
    private String sourcePlaceId;
    private String destinationPlaceId;
    private String sourcePlaceName;
    private String destinationPlaceName;

    private @ServerTimestamp Date createdAt;

    public SearchHistory() {
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public GeoPoint getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(GeoPoint sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public GeoPoint getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(GeoPoint destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getSourcePlaceId() {
        return sourcePlaceId;
    }

    public void setSourcePlaceId(String sourcePlaceId) {
        this.sourcePlaceId = sourcePlaceId;
    }

    public String getDestinationPlaceId() {
        return destinationPlaceId;
    }

    public void setDestinationPlaceId(String destinationPlaceId) {
        this.destinationPlaceId = destinationPlaceId;
    }

    public String getSourcePlaceName() {
        return sourcePlaceName;
    }

    public void setSourcePlaceName(String sourcePlaceName) {
        this.sourcePlaceName = sourcePlaceName;
    }

    public String getDestinationPlaceName() {
        return destinationPlaceName;
    }

    public void setDestinationPlaceName(String destinationPlaceName) {
        this.destinationPlaceName = destinationPlaceName;
    }
}
