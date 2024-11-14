package org.example.demo7.Models;

public class Aircraft {
    private String type;
    private double priceFactor;

    public Aircraft(String type, double priceFactor) {
        this.type = type;
        this.priceFactor = priceFactor;
    }

    public String getType() {
        return type;
    }

    public double getPriceFactor() {
        return priceFactor;
    }
}