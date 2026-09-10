module com.uam.facturationapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens com.uam.facturationapp.application to javafx.fxml;
    opens com.uam.facturationapp.controller to javafx.fxml;

    exports com.uam.facturationapp;
    exports com.uam.facturationapp.application;
}