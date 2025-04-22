package com.example.boilerplateapp.api.models;

import java.io.Serializable;
import java.util.Date;

public class Reservation implements Serializable {
    private String reservation_id;
    private String user_id;
    private String restaurant_id;
    private double restaurant_longitude;
    private double restaurant_latitude;
    private Date reservation_time;
    private int guest_count;
    private String notes;
    private Date created_at;

    public Reservation() {}

    public String getReservationId() {
        return reservation_id;
    }

    public String getUserId() {
        return user_id;
    }

    public String getRestaurantId() {
        return restaurant_id;
    }

    public double getRestaurantLongitude() {
        return restaurant_longitude;
    }

    public double getRestaurantLatitude() {
        return restaurant_latitude;
    }

    public Date getReservationTime() {
        return reservation_time;
    }

    public int getGuestCount() {
        return guest_count;
    }

    public String getNotes() {
        return notes;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setReservationId(String reservation_id) {
        this.reservation_id = reservation_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setRestaurantId(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public void setRestaurantLongitude(double restaurant_longitude) {
        this.restaurant_longitude = restaurant_longitude;
    }

    public void setRestaurantLatitude(double restaurant_latitude) {
        this.restaurant_latitude = restaurant_latitude;
    }

    public void setReservationTime(Date reservation_time) {
        this.reservation_time = reservation_time;
    }

    public void setGuestCount(int guest_count) {
        this.guest_count = guest_count;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }
}
