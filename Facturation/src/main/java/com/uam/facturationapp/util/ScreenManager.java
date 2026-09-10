package com.uam.facturationapp.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class ScreenManager {

    private ScreenManager() {
    }

    public static void openWindow(String resource, String title) throws IOException {
        URL url = ScreenManager.class.getResource(resource);
        if (url == null) {
            throw new IOException("FXML no encontrado: " + resource);
        }

        Parent root = new FXMLLoader(url).load();
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
