package org.example.demo7.Utils;

import org.example.demo7.Models.Aircraft;
import org.example.demo7.Models.Flight;
import org.example.demo7.Models.FlightsDatabase;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataUpdater {
    private final FlightsDatabase flightsDatabase;
    private final ScheduledExecutorService scheduler;
    private final Random random;
    private final Set<String> usedFlightNumbers;
    private final List<String> cities;

    public DataUpdater(FlightsDatabase flightsDatabase) {
        this.flightsDatabase = flightsDatabase;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.random = new Random();
        this.usedFlightNumbers = new HashSet<>();
        this.cities = Arrays.asList("Kyiv", "Lviv", "Odessa", "Kharkiv", "London", "New York", "Paris", "Berlin", "Warsaw", "Milan");
    }

    public void startUpdating() {
        // Запуск оновлення кожні 10 секунд (або інший інтервал на ваш вибір)
        scheduler.scheduleAtFixedRate(this::updateRandomFlight, 0, 10, TimeUnit.SECONDS);
    }

    private void updateRandomFlight() {
        List<Flight> flights = flightsDatabase.getFlights();

        if (flights.isEmpty()) {
            return;
        }

        Flight flight = flights.get(random.nextInt(flights.size()));

        // Випадковий вибір типу оновлення: ціна, розклад, створення чи видалення рейсу
        int actionType = random.nextInt(4); // 0 - ціна, 1 - розклад, 2 - створення, 3 - видалення

        switch (actionType) {
            case 0:
                updateFlightPrice(flight);
                break;
            case 1:
                updateFlightSchedule(flight);
                break;
            case 2:
                createNewFlight();
                break;
            case 3:
                deleteRandomFlight(flight);
                break;
        }
    }

    private void updateFlightPrice(Flight flight) {
        double newPrice = flight.getPrice() * (0.9 + (0.2 * random.nextDouble())); // +/- 10% від початкової ціни
        flightsDatabase.updateFlightPrice(flight.getFlightNumber(), newPrice);
        System.out.println("Updated price for flight " + flight.getFlightNumber() + " to " + newPrice);
    }

    private void updateFlightSchedule(Flight flight) {
        LocalDateTime newDepartureTime = flight.getDepartureTime().plusMinutes(random.nextInt(30) - 15); // +/- 15 хвилин
        LocalDateTime newArrivalTime = flight.getArrivalTime().plusMinutes(random.nextInt(30) - 15); // +/- 15 хвилин
        flightsDatabase.updateFlightSchedule(flight.getFlightNumber(), newDepartureTime, newArrivalTime);
        System.out.println("Updated schedule for flight " + flight.getFlightNumber() +
                " to departure: " + newDepartureTime + ", arrival: " + newArrivalTime);
    }

    public void createNewFlight() {
        String flightNumber = generateUniqueFlightNumber();

        String departureCity = cities.get(random.nextInt(cities.size()));
        String arrivalCity = cities.get(random.nextInt(cities.size()));

        while (departureCity.equals(arrivalCity)) {
            arrivalCity = cities.get(random.nextInt(cities.size()));
        }

        String aircraftType = getRandomAircraftType();

        LocalDateTime departureTime = LocalDateTime.now().plusMinutes(random.nextInt(1440)); // Випадковий час через кілька хвилин
        LocalDateTime arrivalTime = departureTime.plusMinutes(random.nextInt(180) + 60); // Тривалість рейсу від 1 до 4 годин

        Flight newFlight = new Flight(flightNumber, departureCity, arrivalCity, departureTime, arrivalTime, getRandomPrice(), getAircraft(aircraftType));

        flightsDatabase.addFlight(newFlight);
        System.out.println("Created new flight " + flightNumber + " from " + departureCity + " to " + arrivalCity);
    }

    private String generateUniqueFlightNumber() {
        String flightNumber;
        do {
            flightNumber = "PS" + (100 + random.nextInt(900)); // Генерація номера рейсу PSXXX
        } while (usedFlightNumbers.contains(flightNumber)); // Перевірка на унікальність
        usedFlightNumbers.add(flightNumber);
        return flightNumber;
    }

    private String getRandomAircraftType() {
        String[] aircraftTypes = {"Boeing 737", "Airbus A320", "Boeing 787", "Airbus A380"};
        return aircraftTypes[random.nextInt(aircraftTypes.length)];
    }

    private Aircraft getAircraft(String aircraftType) {
        return switch (aircraftType) {
            case "Airbus A320" -> new Aircraft("Airbus A320", 1.1);
            case "Boeing 787" -> new Aircraft("Boeing 787", 1.5);
            case "Airbus A380" -> new Aircraft("Airbus A380", 1.8);
            default -> new Aircraft("Boeing 737", 1.0);
        };
    }

    private double getRandomPrice() {
       return 1000 + (5000 * random.nextDouble());
    }

    private void deleteRandomFlight(Flight flight) {
        flightsDatabase.removeFlight(flight.getFlightNumber());
        System.out.println("Deleted flight: " + flight.getFlightNumber());
    }

    public void stopUpdating() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
