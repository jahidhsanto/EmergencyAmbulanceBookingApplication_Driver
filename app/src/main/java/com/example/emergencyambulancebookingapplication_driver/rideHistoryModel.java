package com.example.emergencyambulancebookingapplication_driver;

public class rideHistoryModel {
    String ambulanceCategory, date, dropOffLatLng, fare, pickUpLatLng, time, distance;

    public rideHistoryModel() {
    }

    public rideHistoryModel(String ambulanceCategory, String date, String dropOffLatLng, String fare, String pickUpLatLng, String time, String distance) {
        this.ambulanceCategory = ambulanceCategory;
        this.date = date;
        this.dropOffLatLng = dropOffLatLng;
        this.fare = fare;
        this.pickUpLatLng = pickUpLatLng;
        this.time = time;
        this.distance = distance;
    }

    public String getAmbulanceCategory() {
        return ambulanceCategory;
    }

    public void setAmbulanceCategory(String ambulanceCategory) {
        this.ambulanceCategory = ambulanceCategory;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDropOffLatLng() {
        return dropOffLatLng;
    }

    public void setDropOffLatLng(String dropOffLatLng) {
        this.dropOffLatLng = dropOffLatLng;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getPickUpLatLng() {
        return pickUpLatLng;
    }

    public void setPickUpLatLng(String pickUpLatLng) {
        this.pickUpLatLng = pickUpLatLng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
