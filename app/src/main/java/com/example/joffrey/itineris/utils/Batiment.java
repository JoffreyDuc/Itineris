package com.example.joffrey.itineris.utils;

public class Batiment {

    private String nom;
    private double latitude;
    private double longitude;

    public Batiment(String nom, double latitude, double longitude){
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNom() {
        return nom;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
