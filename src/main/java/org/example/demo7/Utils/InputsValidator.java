package org.example.demo7.Utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class InputsValidator {

    public static boolean validateInputs(String origin, String destination, String timeField) {
        if (origin.trim().equalsIgnoreCase(destination.trim())) {
            showAlert("Invalid locations.", "Departure and arrival locations must be different.");
            return false;
        }

        if (origin.trim().isEmpty() || destination.trim().isEmpty()) {
            showAlert("Invalid locations.", "Please specify the departure and arrival locations.");
            return false;
        }

        if (!isValidTimeFormat(timeField) && !timeField.isEmpty()) {
            showAlert("Invalid time format", "Enter time in the HH:mm format.");
            return false;
        }

        return true;
    }


    private static boolean isValidTimeFormat(String time) {
        // Перевірка часу на формат HH:mm (де HH - від 00 до 23, mm - від 00 до 59)
        return time.matches("([01]?[0-9]|2[0-3]):([0-5]?[0-9])");
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
