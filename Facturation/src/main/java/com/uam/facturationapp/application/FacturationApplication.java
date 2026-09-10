package com.uam.facturationapp.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FacturationApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = new FXMLLoader(FacturationApplication.class.getResource(
                "/com/uam/facturationapp/fxml/main-menu.fxml")).load();

        stage.setTitle("Sistema de facturación");
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
