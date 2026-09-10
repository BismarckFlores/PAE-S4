package com.uam.facturationapp.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FacturationApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = new FXMLLoader(FacturationApplication.class.getResource("/com/uam/facturationapp/fxml/category-view.fxml")).load();

        stage.setTitle("Facturación");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}
