module org.example.demo7 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens org.example.demo7 to javafx.fxml;
    exports org.example.demo7;
    exports org.example.demo7.Models;
    opens org.example.demo7.Models to javafx.fxml;
    exports org.example.demo7.Utils;
    opens org.example.demo7.Utils to javafx.fxml;
    exports org.example.demo7.Services;
    opens org.example.demo7.Services to javafx.fxml;
    exports org.example.demo7.Controllers;
    opens org.example.demo7.Controllers to javafx.fxml;
    exports org.example.demo7.Windows;
    opens org.example.demo7.Windows to javafx.fxml;
}