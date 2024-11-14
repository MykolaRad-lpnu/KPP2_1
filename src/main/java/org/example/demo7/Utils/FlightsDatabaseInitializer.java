package org.example.demo7.Utils;

import org.example.demo7.Models.FlightsDatabase;
import org.example.demo7.Models.Aircraft;
import org.example.demo7.Models.Flight;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FlightsDatabaseInitializer {

    public static FlightsDatabase initializeDatabase(int flightsNumber) {
        FlightsDatabase database = new FlightsDatabase();

        // Список міст англійською
        List<String> cities = Arrays.asList(
                "Kyiv", "Lviv", "Odesa", "Kharkiv", "London", "New York", "Paris",
                "Berlin", "Warsaw", "Milan", "Budapest"
        );

        // Список літаків
        List<Aircraft> aircrafts = Arrays.asList(
                new Aircraft("Boeing 737", 1.0),
                new Aircraft("Airbus A320", 1.1),
                new Aircraft("Boeing 787", 1.5),
                new Aircraft("Airbus A380", 1.8)
        );

        generateRandomFlights(flightsNumber, cities, aircrafts, database);

        return database;
    }

    private static void generateRandomFlights(int flightsNumber, List<String> cities, List<Aircraft> aircrafts, FlightsDatabase database) {
        Random random = new Random();

        for (int i = 0; i < flightsNumber; i++) {
            // Вибір випадкових міст для рейсу
            String departureCity = cities.get(random.nextInt(cities.size()));
            String destinationCity = cities.get(random.nextInt(cities.size()));

            // Переконатися, що міста різні
            while (departureCity.equals(destinationCity)) {
                destinationCity = cities.get(random.nextInt(cities.size()));
            }

            // Вибір випадкового літака
            Aircraft aircraft = aircrafts.get(random.nextInt(aircrafts.size()));

            // Генерація випадкових часів вильоту та прибуття
            LocalDateTime departureTime = LocalDateTime.of(2024, 11, random.nextInt(30) + 1, random.nextInt(24), random.nextInt(60));
            LocalDateTime arrivalTime = departureTime.plusHours(random.nextInt(5) + 1).plusMinutes(random.nextInt(60));

            // Визначення випадкової ціни
            double price = 1000 + random.nextDouble() * 10000;
            price = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP).doubleValue();
            // Генерація випадкового номера рейсу
            String flightNumber = "PS" + (random.nextInt(900) + 100);

            // Додавання рейсу в базу даних
            database.addFlight(new Flight(flightNumber, departureCity, destinationCity, departureTime, arrivalTime, price, aircraft));
        }
    }

    public static FlightsDatabase initializeDatabase() {
        FlightsDatabase database = new FlightsDatabase();

        // Створення різних типів літаків
        Aircraft boeing737 = new Aircraft("Boeing 737", 1.0);
        Aircraft airbusA320 = new Aircraft("Airbus A320", 1.1);
        Aircraft boeing787 = new Aircraft("Boeing 787", 1.5);
        Aircraft airbusA380 = new Aircraft("Airbus A380", 1.8);

        // Внутрішні рейси
        database.addFlight(new Flight("PS101", "Київ", "Львів",
                LocalDateTime.of(2024, 11, 15, 8, 30),
                LocalDateTime.of(2024, 11, 15, 10, 0),
                1500.00, boeing737));

        database.addFlight(new Flight("PS102", "Львів", "Одеса",
                LocalDateTime.of(2024, 11, 15, 11, 0),
                LocalDateTime.of(2024, 11, 15, 12, 30),
                1800.00, airbusA320));

        database.addFlight(new Flight("PS103", "Одеса", "Київ",
                LocalDateTime.of(2024, 11, 15, 13, 45),
                LocalDateTime.of(2024, 11, 16, 15, 15),
                1600.00, boeing737));

        database.addFlight(new Flight("PS104", "Харків", "Київ",
                LocalDateTime.of(2024, 11, 15, 7, 0),
                LocalDateTime.of(2024, 11, 15, 8, 15),
                1400.00, airbusA320));

        database.addFlight(new Flight("PS105", "Київ", "Харків",
                LocalDateTime.of(2024, 11, 15, 18, 30),
                LocalDateTime.of(2024, 11, 15, 19, 45),
                1450.00, boeing737));

        // Міжнародні рейси
        database.addFlight(new Flight("PS201", "Київ", "Лондон",
                LocalDateTime.of(2024, 11, 17, 9, 0),
                LocalDateTime.of(2024, 11, 17, 12, 0),
                5000.00, boeing787));

        database.addFlight(new Flight("PS202", "Лондон", "Київ",
                LocalDateTime.of(2024, 11, 20, 14, 0),
                LocalDateTime.of(2024, 11, 20, 19, 0),
                5200.00, boeing787));

        database.addFlight(new Flight("PS203", "Київ", "Нью-Йорк",
                LocalDateTime.of(2024, 11, 18, 15, 0),
                LocalDateTime.of(2024, 11, 18, 23, 0),
                12500.00, airbusA380));

        database.addFlight(new Flight("PS204", "Нью-Йорк", "Київ",
                LocalDateTime.of(2024, 11, 25, 10, 0),
                LocalDateTime.of(2024, 11, 26, 4, 0),
                12000.00, airbusA380));

        database.addFlight(new Flight("PS205", "Київ", "Париж",
                LocalDateTime.of(2024, 11, 19, 8, 30),
                LocalDateTime.of(2024, 11, 19, 11, 30),
                6000.00, boeing787));

        database.addFlight(new Flight("PS206", "Париж", "Київ",
                LocalDateTime.of(2024, 11, 23, 16, 0),
                LocalDateTime.of(2024, 11, 23, 19, 0),
                5800.00, airbusA320));

        database.addFlight(new Flight("PS207", "Київ", "Берлін",
                LocalDateTime.of(2024, 11, 20, 13, 0),
                LocalDateTime.of(2024, 11, 20, 15, 0),
                4000.00, boeing737));

        database.addFlight(new Flight("PS208", "Берлін", "Київ",
                LocalDateTime.of(2024, 11, 22, 10, 0),
                LocalDateTime.of(2024, 11, 22, 12, 0),
                4100.00, airbusA320));

        // Додаткові рейси для пересадок
        database.addFlight(new Flight("PS301", "Київ", "Варшава",
                LocalDateTime.of(2024, 11, 21, 8, 0),
                LocalDateTime.of(2024, 11, 21, 9, 30),
                2000.00, boeing737));

        database.addFlight(new Flight("PS302", "Варшава", "Лондон",
                LocalDateTime.of(2024, 11, 21, 11, 0),
                LocalDateTime.of(2024, 11, 21, 12, 30),
                3000.00, airbusA320));

        database.addFlight(new Flight("PS303", "Київ", "Мілан",
                LocalDateTime.of(2024, 11, 21, 14, 0),
                LocalDateTime.of(2024, 11, 21, 16, 0),
                2500.00, boeing737));

        database.addFlight(new Flight("PS304", "Мілан", "Нью-Йорк",
                LocalDateTime.of(2024, 11, 21, 18, 0),
                LocalDateTime.of(2024, 11, 21, 20, 30),
                7000.00, airbusA380));

        database.addFlight(new Flight("PS305", "Львів", "Будапешт",
                LocalDateTime.of(2024, 11, 22, 9, 0),
                LocalDateTime.of(2024, 11, 22, 10, 0),
                2200.00, boeing737));

        database.addFlight(new Flight("PS306", "Будапешт", "Париж",
                LocalDateTime.of(2024, 11, 22, 12, 0),
                LocalDateTime.of(2024, 11, 22, 14, 0),
                3200.00, airbusA320));

        return database;
    }
}
