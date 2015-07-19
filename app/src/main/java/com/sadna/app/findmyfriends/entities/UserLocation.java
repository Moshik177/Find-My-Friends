package com.sadna.app.findmyfriends.entities;

/**
 * Created by avihoo on 27/06/2015.
 */
public class UserLocation {

    private String username;
    private double latitude;
    private double longitude;

    public UserLocation(String username, Double latitude, Double longitude) {
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
