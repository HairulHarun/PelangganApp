package com.gorontalo.chair.pelangganapp.model;

public class LocationsModel {
    public double latitude, longitude;

    public LocationsModel() {
    }

    public LocationsModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
