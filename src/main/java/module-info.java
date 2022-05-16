module com.example.fxdemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires poi;


    opens com.example.fxdemo to javafx.fxml;
    exports com.example.fxdemo;
}