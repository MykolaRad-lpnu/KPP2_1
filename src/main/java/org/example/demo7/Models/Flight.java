package org.example.demo7.Models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public class Flight {
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double basePrice;
    private Aircraft aircraft;

    public Flight(String flightNumber, String origin, String destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime,
                  double basePrice, Aircraft aircraft) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.basePrice = basePrice;
        this.aircraft = aircraft;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public  void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public  void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getPrice() {
        double price = basePrice * aircraft.getPriceFactor();
        return new BigDecimal(price).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public long getDurationInMinutes() {
        return Duration.between(departureTime, arrivalTime).toMinutes();
    }

    public void setPrice(double newPrice) {
        basePrice = newPrice;
    }

}