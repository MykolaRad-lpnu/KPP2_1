import org.example.demo7.Models.Aircraft;
import org.example.demo7.Models.FlightsDatabase;
import org.example.demo7.Models.Flight;
import org.example.demo7.Models.Route;
import org.example.demo7.Services.RouteFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RouteFinderTest {

    private FlightsDatabase flightsDatabase;
    private RouteFinder routeFinder;

    @BeforeEach
    public void setUp() {
        flightsDatabase = new FlightsDatabase();

        flightsDatabase.addFlight(new Flight("UA101", "New York", "Chicago",
                LocalDateTime.of(2024, 12, 10, 10, 0),
                LocalDateTime.of(2024, 12, 10, 12, 0),
                200.0, new Aircraft("Boeing 737", 1.1)));

        flightsDatabase.addFlight(new Flight("AA202", "Chicago", "Los Angeles",
                LocalDateTime.of(2024, 12, 10, 14, 0),
                LocalDateTime.of(2024, 12, 10, 16, 0),
                250.0, new Aircraft("Airbus A320", 1.2)));

        flightsDatabase.addFlight(new Flight("DL303", "New York", "Dallas",
                LocalDateTime.of(2024, 12, 10, 11, 0),
                LocalDateTime.of(2024, 12, 10, 13, 0),
                300.0, new Aircraft("Boeing 757", 1.3)));

        flightsDatabase.addFlight(new Flight("DL404", "Dallas", "Los Angeles",
                LocalDateTime.of(2024, 12, 10, 15, 0),
                LocalDateTime.of(2024, 12, 10, 17, 0),
                280.0, new Aircraft("Airbus A321", 1.15)));

        flightsDatabase.addFlight(new Flight("UA303", "New York", "Los Angeles",
                LocalDateTime.of(2024, 12, 10, 8, 0),
                LocalDateTime.of(2024, 12, 10, 12, 0),
                450.0, new Aircraft("Boeing 777", 1.4)));

        routeFinder = new RouteFinder(flightsDatabase);
    }


    @Test
    public void testDirectFlight() {
        LocalDate date = LocalDate.of(2024, 12, 10);
        LocalTime time = LocalTime.of(7, 0);

        List<Route> routes = routeFinder.findFlightsWithLayovers("New York", "Los Angeles", date, time);

        assertEquals(3, routes.size());

        assertTrue(routes.stream().anyMatch(route ->
                route.getFlights().stream().anyMatch(flight -> "UA303".equals(flight.getFlightNumber()))
        ));
    }

    @Test
    public void testNoFlightsFound() {
        LocalDate date = LocalDate.of(2024, 12, 11);
        LocalTime time = LocalTime.of(9, 0);

        List<Route> routes =
                routeFinder.findFlightsWithLayovers("New York", "San Francisco", date, time);

        assertEquals(0, routes.size());
    }

    @Test
    public void testFindFlightsWithLayovers() {
        LocalDate date = LocalDate.of(2024, 12, 10);
        LocalTime time = LocalTime.of(7, 0);

        flightsDatabase.addFlight(new Flight("UA401", "Los Angeles", "San Diego",
                LocalDateTime.of(2024, 12, 10, 18, 0),
                LocalDateTime.of(2024, 12, 10, 19, 0),
                100.0, new Aircraft("Airbus A321", 1.15)));

        flightsDatabase.addFlight(new Flight("UA501", "San Diego", "Phoenix",
                LocalDateTime.of(2024, 12, 10, 20, 0),
                LocalDateTime.of(2024, 12, 10, 21, 0),
                120.0, new Aircraft("Boeing 777", 1.4)));

        List<Route> routes =
                routeFinder.findFlightsWithLayovers("New York", "Phoenix", date, time);

        assertTrue(routes.stream().anyMatch(route -> route.getLayovers() > 2));
    }

    @Test
    public void testEvaluateRoutesDifferentImportance() {
                Route route1 = new Route(List.of(
                flightsDatabase.getFlightsByNumbers(List.of("UA101")).getFirst(),
                flightsDatabase.getFlightsByNumbers(List.of("AA202")).getFirst()
        ));

        Route route2 = new Route(List.of(
                flightsDatabase.getFlightsByNumbers(List.of("DL303")).getFirst(),
                flightsDatabase.getFlightsByNumbers(List.of("DL404")).getFirst()
        ));

        Route route3 = new Route(List.of(
                flightsDatabase.getFlightsByNumbers(List.of("UA303")).getFirst()
        ));

        List<Route> routes = List.of(route1, route2, route3);

        List<Route> evaluatedRoutes =
                routeFinder.evaluateRoutes(routes, 5, 1, 1);

        assertEquals(3, evaluatedRoutes.size());

        Route highestScoreRoute = evaluatedRoutes.getFirst();
        assertTrue(highestScoreRoute.getFlights()
                        .contains(flightsDatabase.getFlightsByNumbers(List.of("UA303")).getFirst()));
    }

    @Test
    public void testEvaluateRoutesLayoversImportance() {
        flightsDatabase.addFlight(new Flight("UA501", "San Diego", "Phoenix",
                LocalDateTime.of(2024, 12, 10, 20, 0),
                LocalDateTime.of(2024, 12, 10, 21, 0),
                120.0, new Aircraft("Boeing 777", 1.4)));
        
        Route route1 = new Route(List.of(
                flightsDatabase.getFlightsByNumbers(List.of("UA101")).getFirst(),
                flightsDatabase.getFlightsByNumbers(List.of("AA202")).getFirst()
        ));

        Route route2 = new Route(List.of(
                flightsDatabase.getFlightsByNumbers(List.of("DL303")).getFirst(),
                flightsDatabase.getFlightsByNumbers(List.of("DL404")).getFirst()
        ));

        Route route3 = new Route(List.of(
                flightsDatabase.getFlightsByNumbers(List.of("UA303")).getFirst(),
                flightsDatabase.getFlightsByNumbers(List.of("UA501")).getFirst()

        ));


        List<Route> routes = List.of(route1, route2, route3);

        List<Route> evaluatedRoutes =
                routeFinder.evaluateRoutes(routes, 10, 1, 1);

        assertEquals(3, evaluatedRoutes.size());

        Route highestScoreRoute = evaluatedRoutes.getFirst();
        assertTrue(highestScoreRoute.getFlights()
                .contains(flightsDatabase.getFlightsByNumbers(List.of("UA101")).getFirst()));
    }

}
