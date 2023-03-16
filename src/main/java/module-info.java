module com.example.milestone2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;


    opens com.example.milestone2 to javafx.fxml;
    exports com.example.milestone2;
}