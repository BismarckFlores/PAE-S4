package com.uam.facturationapp.controller;

import com.uam.facturationapp.util.ScreenManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainMenuController {

    @FXML private BorderPane contenedor;

    @FXML
    private void initialize() {
        ScreenManager.setContenedor(contenedor);
    }

    @FXML
    private void abrirProductos() {
        try {
            ScreenManager.mostrarVista("/com/uam/facturationapp/fxml/product-view.fxml");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "No fue posible abrir Productos.\n" + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void salir() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Desea cerrar la aplicación?", ButtonType.OK, ButtonType.CANCEL);
        if (alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Platform.exit();
        }
    }
}
