package org.example.demo7.Controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.textfield.TextFields;
import org.example.demo7.Models.Flight;
import org.example.demo7.Models.FlightDisplay;
import org.example.demo7.Models.FlightsDatabase;
import org.example.demo7.Models.Route;
import org.example.demo7.Services.RouteFinder;
import org.example.demo7.Utils.*;
import org.example.demo7.Windows.FlightDetailsWindow;

public class Controller implements FlightChangeListener {
    @FXML
    private Spinner durationImportanceSpinner;
    @FXML
    private Spinner layoversImportanceSpinner;
    @FXML
    private Spinner priceImportanceSpinner;
    @FXML
    private TextField originField;
    @FXML
    private TextField destinationField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField timeField;
    @FXML
    private TableView<FlightDisplay> flightTable;
    @FXML
    private RangeSlider priceSlider;
    @FXML
    private TextField priceFromField;
    @FXML
    private TextField priceToField;
    @FXML
    private RangeSlider layoversSlider;
    @FXML
    private TextField layoverFromField;
    @FXML
    private TextField layoverToField;
    @FXML
    private RangeSlider durationSlider;
    @FXML
    private TextField durationFromField;
    @FXML
    private TextField durationToField;
    @FXML
    private TableColumn<FlightDisplay, String> flightNumberCol;
    @FXML
    private TableColumn<FlightDisplay, String> originCol;
    @FXML
    private TableColumn<FlightDisplay, String> destinationCol;
    @FXML
    private TableColumn<FlightDisplay, String> departureCol;
    @FXML
    private TableColumn<FlightDisplay, String> arrivalCol;
    @FXML
    private TableColumn<FlightDisplay, String> durationCol;
    @FXML
    private TableColumn<FlightDisplay, Integer> layoverCountCol;
    @FXML
    private TableColumn<FlightDisplay, Number> priceCol;
    @FXML
    private TableColumn<FlightDisplay, String> detailsCol;

    private FlightsDatabase database;
    private RouteFinder routeFinder;
    private DataUpdater dataUpdater;

    private final ObservableList<FlightDisplay> displayFlights = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initializeDatabase();
        initializeTable();
        initializeAutoCompletion();
        initializeSliders();
        updatePriceFields();
        updateLayoverFields();
        updateDurationFields();
    }

    @Override
    public void onFlightChange(Flight updatedFlight) {
        displayFlights.forEach(displayFlight -> {
            if (displayFlight.getFlightNumbers().contains(updatedFlight.getFlightNumber())) {
                displayFlight.updateFromFlight(updatedFlight);
            }
        });
    }

    @FXML
    public void onClose() {
        stopDataUpdater();
    }

    @FXML
    private void handleSearchButton() {
        if (!InputsValidator.validateInputs(originField.getText(),
                destinationField.getText(), timeField.getText())) {
            return;
        }

        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        LocalDate date = datePicker.getValue();
        LocalTime time = null;

        if (!timeField.getText().isEmpty())
            time = LocalTime.parse(timeField.getText());

        List<Route> routes = routeFinder.findFlightsWithLayovers(origin, destination, date, time);
        displayFlights(routes);
    }

    @FXML
    public void handleFindOptimalButton(ActionEvent actionEvent) {
        if (!InputsValidator.validateInputs(originField.getText(),
                destinationField.getText(), timeField.getText())) {
            return;
        }

        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        LocalDate date = datePicker.getValue();
        LocalTime time = null;

        int priceImportance = (int) priceImportanceSpinner.getValue();
        int layoversImportance = (int) layoversImportanceSpinner.getValue();
        int durationImportance = (int) durationImportanceSpinner.getValue();

        if (!timeField.getText().isEmpty())
            time = LocalTime.parse(timeField.getText());

        List<Route> routes = routeFinder.findFlightsWithLayovers(origin, destination, date, time);
        routes = routeFinder.evaluateRoutes(routes, priceImportance, layoversImportance, durationImportance);

        displayFlights(routes);
    }

    @FXML
    private void handlePriceSliderChange() {
        updatePriceFields();
    }

    @FXML
    private void handleLayoversSliderChange() {
        updateLayoverFields();
    }

    @FXML
    private void handleDurationSliderChange() {
        updateDurationFields();
    }

    @FXML
    private void handleFilterFlightsButton() {
        validateAndUpdateSliders();

        double lowPrice = priceSlider.getLowValue();
        double highPrice = priceSlider.getHighValue();
        int lowLayovers = (int) layoversSlider.getLowValue();
        int highLayovers = (int) layoversSlider.getHighValue();
        int lowDurationHours = (int) durationSlider.getLowValue();
        int highDurationHours = (int) durationSlider.getHighValue();


        flightTable.setItems(database.filterFlights(lowPrice, highPrice,
                lowLayovers, highLayovers, lowDurationHours, highDurationHours, displayFlights));
    }

    private void initializeDatabase() {
        database = FlightsDatabaseInitializer.initializeDatabase(50);
        routeFinder = new RouteFinder(database);
        database.addListener(this);
        dataUpdater = new DataUpdater(database);
        dataUpdater.startUpdating();
    }

    private void initializeTable() {
        flightTable.setItems(displayFlights);

        flightNumberCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFlightNumbers().getFirst()));
        originCol.setCellValueFactory(cellData -> cellData.getValue().getOriginProperty());
        destinationCol.setCellValueFactory(cellData -> cellData.getValue().getDestinationProperty());
        departureCol.setCellValueFactory(cellData -> cellData.getValue().getDepartureProperty());
        arrivalCol.setCellValueFactory(cellData -> cellData.getValue().getArrivalProperty());
        priceCol.setCellValueFactory(cellData -> cellData.getValue().getPriceProperty());
        durationCol.setCellValueFactory(cellData -> cellData.getValue().getDurationProperty());
        layoverCountCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLayoverCount()));
        setDetailsButtonInTable();
    }

    private void setDetailsButtonInTable() {
        detailsCol.setCellFactory(param -> new TableCell<FlightDisplay, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Button btn = new Button("Show Details");
                    btn.setOnAction(event -> new FlightDetailsWindow(database).showDetails(getTableRow().getItem()));
                    setGraphic(btn);
                }
            }
        });
    }

    private void initializeAutoCompletion() {
        List<String> origins = database.getFlights().stream()
                .map(Flight::getOrigin)
                .distinct()
                .collect(Collectors.toList());

        List<String> destinations = database.getFlights().stream()
                .map(Flight::getDestination)
                .distinct()
                .collect(Collectors.toList());

        TextFields.bindAutoCompletion(originField, origins);
        TextFields.bindAutoCompletion(destinationField, destinations);

        datePicker.setEditable(false);
    }

    private void initializeSliders() {
        List<Double> prices = displayFlights.stream()
                .map(FlightDisplay::getPrice)
                .toList();

        SliderHelper.updatePriceSlider(priceSlider, prices);
    }

    public void stopDataUpdater() {
        if (dataUpdater != null) {
            dataUpdater.stopUpdating();
        }
    }

    private void displayFlights(List<Route> routes) {
        displayFlights.clear();
        for (Route route : routes) {
            Flight firstFlight = route.getFlights().getFirst();
            Flight lastFlight = route.getFlights().getLast();
            long durationMinutes = route.getTotalDuration();
            double totalPrice = route.getTotalPrice();
            int layoverCount = route.getLayovers();

            List<String> flightNumbers = route.getFlights().stream()
                    .map(Flight::getFlightNumber)
                    .collect(Collectors.toList());

            FlightDisplay displayFlight = new FlightDisplay(
                    flightNumbers,
                    firstFlight.getOrigin(),
                    lastFlight.getDestination(),
                    firstFlight.getDepartureTime(),
                    lastFlight.getArrivalTime(),
                    totalPrice,
                    durationMinutes,
                    layoverCount
            );

            displayFlights.add(displayFlight);
        }
        flightTable.setItems(displayFlights);
    }

    private void updatePriceFields() {
        priceFromField.setText(String.valueOf((int) priceSlider.getLowValue()));
        priceToField.setText(String.valueOf((int) priceSlider.getHighValue()));
    }

    private void updateLayoverFields() {
        layoverFromField.setText(String.valueOf((int) layoversSlider.getLowValue()));
        layoverToField.setText(String.valueOf((int) layoversSlider.getHighValue()));
    }

    private void updateDurationFields() {
        durationFromField.setText(String.valueOf((int) durationSlider.getLowValue()));
        durationToField.setText(String.valueOf((int) durationSlider.getHighValue()));
    }

    private void validateAndUpdateSliders() {
        double lowPrice = SliderHelper.
                parseDoubleOrDefault(priceFromField.getText(), priceSlider.getLowValue());
        double highPrice = SliderHelper.
                parseDoubleOrDefault(priceToField.getText(), priceSlider.getHighValue());
        int lowLayovers = SliderHelper.
                parseIntOrDefault(layoverFromField.getText(), (int) layoversSlider.getLowValue());
        int highLayovers = SliderHelper.
                parseIntOrDefault(layoverToField.getText(), (int) layoversSlider.getHighValue());
        int lowDurationHours = SliderHelper.
                parseIntOrDefault(durationFromField.getText(), (int) durationSlider.getLowValue());
        int highDurationHours = SliderHelper.
                parseIntOrDefault(durationToField.getText(), (int) durationSlider.getHighValue());

        SliderHelper.updateSlider(priceSlider, lowPrice, highPrice);
        updatePriceFields();

        SliderHelper.updateSlider(layoversSlider, lowLayovers, highLayovers);
        updateLayoverFields();

        SliderHelper.updateSlider(durationSlider, lowDurationHours, highDurationHours);
        updateDurationFields();
    }
}