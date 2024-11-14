package org.example.demo7.Models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FlightDisplay {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final SimpleStringProperty origin;
    private final SimpleStringProperty destination;
    private final SimpleStringProperty departure;
    private final SimpleStringProperty arrival;
    private final SimpleStringProperty duration;
    private final SimpleDoubleProperty price;
    private final SimpleIntegerProperty layoverCount;
    private final List<String> flightNumbers;

    public FlightDisplay(List<String> flightNumbers, String origin, String destination, LocalDateTime departure, LocalDateTime arrival, double price, long durationMinutes, int layoverCount) {
        this.flightNumbers = flightNumbers;
        this.origin = new SimpleStringProperty(origin);
        this.destination = new SimpleStringProperty(destination);
        this.departure = new SimpleStringProperty(formatDateTime(departure));
        this.arrival = new SimpleStringProperty(formatDateTime(arrival));
        this.price = new SimpleDoubleProperty(new BigDecimal(price).setScale(2, RoundingMode.HALF_UP).doubleValue());
        this.duration = new SimpleStringProperty(durationString(durationMinutes));
        this.layoverCount = new SimpleIntegerProperty(layoverCount);
    }

    public String getOrigin() { return origin.get(); }
    public String getDestination() { return destination.get(); }
    public String getDeparture() { return departure.get(); }
    public String getArrival() { return arrival.get(); }
    public double getPrice() { return price.get(); }
    public int getLayoverCount() { return layoverCount.get(); }
    public List<String> getFlightNumbers() { return flightNumbers; }
    public String getDuration() { return duration.get(); }

    public SimpleStringProperty getOriginProperty() { return origin; }
    public SimpleStringProperty getDestinationProperty() { return destination; }
    public SimpleStringProperty getDepartureProperty() { return departure; }
    public SimpleStringProperty getArrivalProperty() { return arrival; }
    public SimpleDoubleProperty getPriceProperty() { return price; }
    public SimpleIntegerProperty getLayoverCountProperty() { return layoverCount; }
    public SimpleStringProperty getDurationProperty() { return duration; }

    public void updateFromFlight(Flight updatedFlight) {
        this.price.set(updatedFlight.getPrice());
        this.departure.set(formatDateTime(updatedFlight.getDepartureTime()));
        this.arrival.set(formatDateTime(updatedFlight.getArrivalTime()));

        long durationMinutes = java.time.Duration.between(
                updatedFlight.getDepartureTime(),
                updatedFlight.getArrivalTime()
        ).toMinutes();
        this.duration.set(durationString(durationMinutes));
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    private String durationString(long minutes) {
        return String.format("%d hours, %d minutes", minutes / 60, minutes % 60);
    }
}
