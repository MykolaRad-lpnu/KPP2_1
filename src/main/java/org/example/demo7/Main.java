package org.example.demo7;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.demo7.Controllers.Controller;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("flight-app.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 700);
        stage.setTitle("FlightApp");
        stage.setScene(scene);
        Controller controller = fxmlLoader.getController();
        stage.setOnCloseRequest(event -> {
            controller.onClose(); // Викликайте ваш метод
            Platform.exit(); // Завершити програму
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}