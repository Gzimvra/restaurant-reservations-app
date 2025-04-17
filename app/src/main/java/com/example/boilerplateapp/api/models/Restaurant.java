package com.example.boilerplateapp.api.models;

public class Restaurant {
    private final String name;
    private final double latitude;
    private final double longitude;
    private final String city;
    private final String street;
    private final String houseNumber;
    private final String cuisine;
    private final String phone;
    private final String website;

    // Constructor
    public Restaurant(String name, double latitude, double longitude, String city,
                      String street, String houseNumber, String cuisine, String phone, String website) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.cuisine = cuisine;
        this.phone = phone;
        this.website = website;
    }

    // Getters (add setters if needed)
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getCity() { return city; }
    public String getStreet() { return street; }
    public String getHouseNumber() { return houseNumber; }
    public String getCuisine() { return cuisine; }
    public String getPhone() { return phone; }
    public String getWebsite() { return website; }
}

