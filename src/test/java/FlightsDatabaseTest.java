import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.demo7.Models.Aircraft;
import org.example.demo7.Models.Flight;
import org.example.demo7.Models.FlightDisplay;
import org.example.demo7.Models.FlightsDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class FlightsDatabaseTest {

    private FlightsDatabase flightsDatabase;
    private Flight testFlight1;
    private Flight testFlight2;
    private Flight testFlight3;

    @BeforeEach
    public void setUp() {
        flightsDatabase = new FlightsDatabase();

        Aircraft aircraft1 = new Aircraft("Boeing 737", 1.2); // приклад параметрів для літаків
        Aircraft aircraft2 = new Aircraft("Airbus A320", 1.1);
        Aircraft aircraft3 = new Aircraft("Boeing 787", 1.5);

        testFlight1 = new Flight("UA101", "New York", "Los Angeles",
                LocalDateTime.of(2024, 12, 10, 14, 30),
                LocalDateTime.of(2024, 12, 10, 16, 30),
                500.0, aircraft1);
        testFlight2 = new Flight("AA202", "New York", "Chicago",
                LocalDateTime.of(2024, 12, 12, 10, 0),
                LocalDateTime.of(2024, 12, 12, 12, 0),
                300.0, aircraft2);
        testFlight3 = new Flight("DL303", "Los Angeles", "New York",
                LocalDateTime.of(2024, 12, 15, 9, 0),
                LocalDateTime.of(2024, 12, 15, 17, 0),
                450.0, aircraft3);

        flightsDatabase.addFlight(testFlight1);
        flightsDatabase.addFlight(testFlight2);
        flightsDatabase.addFlight(testFlight3);
    }

    @Test
    public void testAddFlight() {
        Aircraft newAircraft = new Aircraft("Airbus A350", 1.3);
        Flight newFlight = new Flight("BA404", "London", "Paris",
                LocalDateTime.of(2024, 12, 20, 8, 0),
                LocalDateTime.of(2024, 12, 20, 10, 0),
                150.0, newAircraft);
        flightsDatabase.addFlight(newFlight);
        assertTrue(flightsDatabase.getFlights().contains(newFlight));

        Flight duplicateFlight = new Flight("BA404", "Lviv", "Paris", LocalDateTime.now(), LocalDateTime.now().plusHours(5), 300.0, newAircraft);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            flightsDatabase.addFlight(duplicateFlight);
        });

        assertEquals("A flight with this flight number already exists.", exception.getMessage());
    }

    @Test
    public void testUpdateFlightPrice() {
        flightsDatabase.updateFlightPrice("AA202", 350.0);
        double newPrice = 350.0 * testFlight2.getAircraft().getPriceFactor();
        newPrice = new BigDecimal(newPrice).setScale(2, RoundingMode.HALF_UP).doubleValue();
        assertEquals(newPrice, testFlight2.getPrice());
    }

    @Test
    public void testUpdateFlightScheduleWithValidTimes() {
        LocalDateTime newDepartureTime = LocalDateTime.of(2024, 12, 12, 12, 30);
        LocalDateTime newArrivalTime = LocalDateTime.of(2024, 12, 12, 14, 30);

        flightsDatabase.updateFlightSchedule("AA202", newDepartureTime, newArrivalTime);
        assertEquals(newDepartureTime, testFlight2.getDepartureTime());
        assertEquals(newArrivalTime, testFlight2.getArrivalTime());
    }

    @Test
    public void testUpdateFlightScheduleWithInvalidTimes() {
        LocalDateTime invalidDepartureTime = LocalDateTime.of(2024, 12, 12, 16, 30);
        LocalDateTime invalidArrivalTime = LocalDateTime.of(2024, 12, 12, 14, 30);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            flightsDatabase.updateFlightSchedule("AA202", invalidDepartureTime, invalidArrivalTime);
        });

        assertEquals("Departure time cannot be later than arrival time.", exception.getMessage());
    }


    @Test
    public void testGetFlightsByOriginAndDestination() {
        Flight flight3 = new Flight("BK405", "New York", "Chicago", LocalDateTime.now().plusHours(4), LocalDateTime.now().plusHours(6), 350.0, new Aircraft("Boeing 757", 1.3));
        flightsDatabase.addFlight(flight3);

        List<Flight> flights = flightsDatabase.getFlights("New York", "Chicago", null, null);
        assertEquals(2, flights.size());

        boolean hasAA202 = flights.stream().anyMatch(flight -> "AA202".equals(flight.getFlightNumber()));
        boolean hasBK405 = flights.stream().anyMatch(flight -> "BK405".equals(flight.getFlightNumber()));

        assertTrue(hasAA202);
        assertTrue(hasBK405);

        flights.forEach(flight -> {
            assertEquals("New York", flight.getOrigin());
            assertEquals("Chicago", flight.getDestination());
        });
    }

    @Test
    public void testGetFlightsByDateAndTime() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 10, 14, 30);

        Flight flight2 = new Flight("PS301", "New York", "Los Angeles", dateTime, dateTime.plusHours(6), 550.0, new Aircraft("Airbus A320", 1.1));
        Flight flight3 = new Flight("PS302", "New York", "Los Angeles", dateTime.plusDays(1), dateTime.plusDays(1).plusHours(5), 600.0, new Aircraft("Boeing 747", 1.3));
        Flight flight4 = new Flight("PS303", "New York", "Los Angeles", dateTime.minusDays(1), dateTime.minusDays(1).plusHours(5), 620.0, new Aircraft("Boeing 737", 1.4));

        flightsDatabase.addFlight(flight2);
        flightsDatabase.addFlight(flight3);
        flightsDatabase.addFlight(flight4);

        List<Flight> flights = flightsDatabase.getFlights("New York", "Los Angeles", dateTime.toLocalDate(), dateTime.toLocalTime());

        assertEquals(2, flights.size());
        assertTrue(flights.stream().anyMatch(flight -> "UA101".equals(flight.getFlightNumber())));
        assertTrue(flights.stream().anyMatch(flight -> "PS301".equals(flight.getFlightNumber())));

        assertFalse(flights.stream().anyMatch(flight -> "PS302".equals(flight.getFlightNumber())));
        assertFalse(flights.stream().anyMatch(flight -> "PS303".equals(flight.getFlightNumber())));
    }

    @Test
    public void testFilterFlights() {
        ObservableList<FlightDisplay> flightDisplays = FXCollections.observableArrayList(
                new FlightDisplay(
                        List.of("UA101"), "New York", "Los Angeles",
                        LocalDateTime.of(2024, 12, 10, 14, 30),
                        LocalDateTime.of(2024, 12, 10, 16, 30),
                        500.0, 120, 1
                ),
                new FlightDisplay(
                        List.of("AA202"), "New York", "Chicago",
                        LocalDateTime.of(2024, 12, 12, 10, 0),
                        LocalDateTime.of(2024, 12, 12, 12, 0),
                        300.0, 120, 0
                ),
                new FlightDisplay(
                        List.of("DL303"), "Los Angeles", "New York",
                        LocalDateTime.of(2024, 12, 15, 9, 0),
                        LocalDateTime.of(2024, 12, 15, 17, 0),
                        450.0, 480, 2
                )
        );

        ObservableList<FlightDisplay> filteredFlights = flightsDatabase.filterFlights(
                200.0, 500.0, 0, 2, 1, 5, flightDisplays
        );

        assertEquals(2, filteredFlights.size());
        assertTrue(filteredFlights.stream().anyMatch(flight -> "UA101".equals(flight.getFlightNumbers().getFirst())));
        assertTrue(filteredFlights.stream().anyMatch(flight -> "AA202".equals(flight.getFlightNumbers().getFirst())));
        }

    @Test
    public void testGetFlightsByFlightNumbers() {
        List<Flight> flights = flightsDatabase.getFlightsByNumbers(Arrays.asList("UA101", "DL303"));
        assertEquals(2, flights.size());
        assertTrue(flights.stream().anyMatch(flight -> flight.getFlightNumber().equals("UA101")));
        assertTrue(flights.stream().anyMatch(flight -> flight.getFlightNumber().equals("DL303")));
    }

    @Test
    public void testRemoveFlight() {
        flightsDatabase.removeFlight("UA101");
        assertFalse(flightsDatabase.getFlights().stream().anyMatch(flight -> flight.getFlightNumber().equals("UA101")));
    }
}
