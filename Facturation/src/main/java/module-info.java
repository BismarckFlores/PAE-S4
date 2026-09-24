module com.uam.facturationapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires org.postgresql.jdbc;

    opens com.uam.facturationapp.application to javafx.fxml;
    opens com.uam.facturationapp.controller to javafx.fxml;
    opens com.uam.facturationapp.model to javafx.base;

    exports com.uam.facturationapp;
    exports com.uam.facturationapp.application;
}