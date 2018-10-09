package me.varunon9.saathmetravel.models;

import com.google.firebase.firestore.GeoPoint;

import me.varunon9.saathmetravel.constants.AppConstants;

public class User {

    private String email;
    private String gender;
    private GeoPoint location;
    private String mobile;
    private String name;
    private String preference;
    private String uid;

    public User() {
        preference = AppConstants.userDefaultPreference;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
