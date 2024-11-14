package org.example.demo7.Services;

import org.example.demo7.Models.FlightsDatabase;
import org.example.demo7.Models.Flight;
import org.example.demo7.Models.Route;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class RouteFinder {
    private FlightsDatabase database;

    public RouteFinder(FlightsDatabase database) {
        this.database = database;
    }

    public List<Route> findFlightsWithLayovers(String origin, String destination,
                                               LocalDate date, LocalTime time) {
        final int maxLayovers = 10;
        List<Route> routes = new ArrayList<>();
        List<Flight> currentRoute = new ArrayList<>();

        findRoutesRecursive(origin, destination, date, time, maxLayovers, currentRoute, routes);
        return routes;
    }

    private void findRoutesRecursive(String currentOrigin, String destination, LocalDate date,
                                     LocalTime time, int remainingLayovers, List<Flight> currentRoute,
                                     List<Route> routes) {
        List<Flight> possibleFlights = database.getFlights(currentOrigin, null, date, time);

        for (Flight flight : possibleFlights) {
            if (currentRoute.contains(flight)) {
                continue;
            }

            currentRoute.add(flight);

            if (flight.getDestination().equalsIgnoreCase(destination)) {
                routes.add(new Route(new ArrayList<>(currentRoute)));
            } else if (remainingLayovers > 0) {
                LocalDate nextDate = flight.getArrivalTime().toLocalDate();
                LocalTime nextTime = flight.getArrivalTime().toLocalTime();

                findRoutesRecursive(flight.getDestination(), destination, nextDate,
                        nextTime, remainingLayovers - 1, currentRoute, routes);
            }

            currentRoute.remove(currentRoute.size() - 1);
        }
    }

    public List<Route> evaluateRoutes(List<Route> routes, int priceImportance,
                                      int layoversImportance, int durationImportance) {

        double maxPrice = routes.stream().mapToDouble(Route::getTotalPrice).max().orElse(0);
        int maxLayovers = routes.stream().mapToInt(Route::getLayovers).max().orElse(0);
        long maxDuration = routes.stream().mapToLong(Route::getTotalDuration).max().orElse(0);

        Map<Route, Double> routeScores = new HashMap<>();

        for (Route route : routes) {
            double score = calculateRouteScore(route, maxPrice, maxLayovers, maxDuration,
                    priceImportance, layoversImportance, durationImportance);
            routeScores.put(route, score);
        }

        return routeScores.entrySet().stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }


    private double calculateRouteScore(Route route, double maxPrice, int maxLayovers,
                                       long maxDuration, int priceImportance,
                                       int layoversImportance, int durationImportance) {
        double normalizedPrice = route.getTotalPrice() / maxPrice;
        double normalizedLayovers = (double) route.getLayovers() / (maxLayovers + 1);
        double normalizedDuration = (double) route.getTotalDuration() / maxDuration;

        return (priceImportance * (1 / normalizedPrice)) +
                (layoversImportance * (1 / normalizedLayovers)) +
                (durationImportance * (1 / normalizedDuration));
    }
}
