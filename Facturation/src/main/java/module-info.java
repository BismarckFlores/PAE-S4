module com.uam.facturationapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens com.uam.facturationapp to javafx.fxml;
    exports com.uam.facturationapp;
}