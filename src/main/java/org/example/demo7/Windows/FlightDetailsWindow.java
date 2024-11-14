package org.example.demo7.Windows;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.demo7.Models.Flight;
import org.example.demo7.Models.FlightDisplay;
import org.example.demo7.Models.FlightsDatabase;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class FlightDetailsWindow {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    private FlightsDatabase database;

    public FlightDetailsWindow(FlightsDatabase database) {
        this.database = database;
    }
    public void showDetails(FlightDisplay flightDisplay) {
        List<Flight> route = database.getFlightsByNumbers(flightDisplay.getFlightNumbers());
        Stage detailStage = new Stage();
        VBox detailLayout = new VBox(10);

        detailLayout.setPadding(new Insets(10));

        Label title = new Label("Route Details for Flight " + flightDisplay.getFlightNumbers().getFirst());
        detailLayout.getChildren().add(title);

        TableView<Flight> detailTable = createDetailTable(route);
        detailLayout.getChildren().add(detailTable);

        detailStage.setScene(new Scene(detailLayout, 650, 400));
        detailStage.setTitle("Flight Segment Details");
        detailStage.show();
    }

    private TableView<Flight> createDetailTable(List<Flight> route) {
        // Таблиця для деталей кожного сегмента
        TableView<Flight> detailTable = new TableView<>();

        TableColumn<Flight, String> flightNumberCol = new TableColumn<>("Flight Number");
        flightNumberCol.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));

        TableColumn<Flight, String> originCol = new TableColumn<>("Origin");
        originCol.setCellValueFactory(new PropertyValueFactory<>("origin"));

        TableColumn<Flight, String> destinationCol = new TableColumn<>("Destination");
        destinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));

        TableColumn<Flight, String> departureTimeCol = new TableColumn<>("Departure");
        departureTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartureTime().format(DATE_FORMATTER)));

        TableColumn<Flight, String> arrivalTimeCol = new TableColumn<>("Arrival");
        arrivalTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getArrivalTime().format(DATE_FORMATTER)));

        TableColumn<Flight, String> aircraftTypeCol = new TableColumn<>("Aircraft Type");
        aircraftTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAircraft().getType()));

        TableColumn<Flight, String> durationCol = new TableColumn<>("Duration");
        durationCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        String.format("%d hours, %d minutes",
                                cellData.getValue().getDurationInMinutes() / 60,
                                cellData.getValue().getDurationInMinutes() % 60)
                )
        );

        TableColumn<Flight, Number> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()));

        detailTable.getColumns().addAll(flightNumberCol, originCol, destinationCol, departureTimeCol, arrivalTimeCol, aircraftTypeCol, priceCol);

        for (Flight segment : route) {
            detailTable.getItems().add(segment);
        }

        return detailTable;
    }
}
