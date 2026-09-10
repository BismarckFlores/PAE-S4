package com.uam.facturationapp.controller;

import com.uam.facturationapp.util.ScreenManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private void onOpenProducts() {
        try {
            ScreenManager.openWindow("/com/uam/facturationapp/fxml/product-view.fxml",
                    "Gestión de productos");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "No fue posible abrir Productos.\n" + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Desea cerrar la aplicación?", ButtonType.OK, ButtonType.CANCEL);
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Platform.exit();
        }
    }
}
