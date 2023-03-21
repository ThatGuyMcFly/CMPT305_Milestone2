module com.example.milestone2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;


    opens com.example.milestone3 to javafx.fxml;
    exports com.example.milestone3;
}