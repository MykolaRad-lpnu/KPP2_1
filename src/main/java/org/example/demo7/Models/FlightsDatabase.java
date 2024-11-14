package org.example.demo7.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.demo7.Utils.FlightChangeListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class FlightsDatabase {
    private List<Flight> flights = new ArrayList<>();
    private List<FlightChangeListener> listeners = new ArrayList<>();

    public void addListener(FlightChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(FlightChangeListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(Flight updatedFlight) {
        for (FlightChangeListener listener : listeners) {
            listener.onFlightChange(updatedFlight);
        }
    }

    public void updateFlightPrice(String flightNumber, double newPrice) {
        flights.stream()
                .filter(flight -> flight.getFlightNumber().equals(flightNumber))
                .findFirst()
                .ifPresent(flight -> {
                    flight.setPrice(newPrice);
                    notifyListeners(flight);
                });
    }

    public void updateFlightSchedule(String flightNumber, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        if (departureTime.isAfter(arrivalTime)) {
            throw new IllegalArgumentException("Departure time cannot be later than arrival time.");
        }

        flights.stream()
                .filter(flight -> flight.getFlightNumber().equals(flightNumber))
                .findFirst()
                .ifPresent(flight -> {
                    flight.setArrivalTime(arrivalTime);
                    flight.setDepartureTime(departureTime);
                    notifyListeners(flight);
                });
    }

    public void addFlight(Flight flight) {
        boolean flightExists = flights.stream()
                .anyMatch(existingFlight -> existingFlight.getFlightNumber().equals(flight.getFlightNumber()));

        if (flightExists) {
            throw new IllegalArgumentException("A flight with this flight number already exists.");
        }

        flights.add(flight);
    }


    public List<Flight> getFlights() {
        return flights;
    }

    public List<Flight> getFlights(String origin, String destination, LocalDate date, LocalTime time) {
        List<Flight> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Flight flight : flights) {
            LocalDateTime departureTime = flight.getDepartureTime();
            boolean matchesDate = (date == null || departureTime.toLocalDate().equals(date));
            boolean matchesTime = (time == null || departureTime.toLocalTime().equals(time)
                    || departureTime.toLocalTime().isAfter(time));

            boolean matchesOrigin = flight.getOrigin().equalsIgnoreCase(origin);
            boolean matchesDestination = (destination == null || flight.getDestination().equalsIgnoreCase(destination));

            if (matchesOrigin && matchesDestination && departureTime.isAfter(now) && matchesDate && matchesTime) {
                result.add(flight);
            }
        }
        return result;
    }

    public List<Flight> getFlightsByNumbers(List<String> flightNumbers) {
        return flights.stream()
                .filter(flight -> flightNumbers.contains(flight.getFlightNumber()))
                .collect(Collectors.toList());
    }

    public void removeFlight(String flightNumber) {
        flights.removeIf(flight -> flight.getFlightNumber().equals(flightNumber));
    }

    public ObservableList<FlightDisplay> filterFlights(double lowPrice, double highPrice,
                                                       int lowLayovers, int highLayovers,
                                                       int lowDurationHours, int highDurationHours,
                                                       ObservableList<FlightDisplay> flights) {
        return flights.stream()
                .filter(flight -> flight.getPrice() >= lowPrice && flight.getPrice() <= highPrice)
                .filter(flight -> flight.getLayoverCount() >= lowLayovers && flight.getLayoverCount() <= highLayovers)
                .filter(flight -> {
                    long totalDurationMinutes = calculateTotalDuration(getFlightsByNumbers(flight.getFlightNumbers()));
                    int totalDurationHours = (int) (totalDurationMinutes / 60);
                    return totalDurationHours >= lowDurationHours && totalDurationHours <= highDurationHours;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private long calculateTotalDuration(List<Flight> route) {
        return route.stream()
                .mapToLong(Flight::getDurationInMinutes)
                .sum();
    }
}