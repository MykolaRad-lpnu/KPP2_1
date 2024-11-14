package org.example.demo7.Models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Route {
    private final List<Flight> flights;
    private final double totalPrice;
    private final long totalDuration;
    private final int layovers;

    public Route(List<Flight> flights) {
        this.flights = flights;
        this.totalPrice = calculateTotalPrice();
        this.totalDuration = calculateTotalDuration();
        this.layovers = flights.size() - 1;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public int getLayovers() {
        return layovers;
    }

    private double calculateTotalPrice() {
        return flights.stream().mapToDouble(Flight::getPrice).sum();
    }

    private long calculateTotalDuration() {
        LocalDateTime departureTime = flights.getFirst().getDepartureTime();
        LocalDateTime arrivalTime = flights.getLast().getArrivalTime();
        return Duration.between(departureTime, arrivalTime).toMinutes();
    }
}
