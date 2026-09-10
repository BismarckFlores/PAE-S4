package com.uam.facturationapp.controller;

import com.uam.facturationapp.util.Mensajes;
import com.uam.facturationapp.util.ScreenManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainMenuController {

    private static final String FXML = "/com/uam/facturationapp/fxml/";

    @FXML private BorderPane contenedor;

    @FXML
    private void initialize() {
        ScreenManager.setContenedor(contenedor);
    }

    @FXML
    private void abrirCategorias() {
        abrir("category-view.fxml", "Categorías");
    }

    @FXML
    private void abrirProductos() {
        abrir("product-view.fxml", "Productos");
    }

    @FXML
    private void abrirCargos() {
        abrir("cargo-view.fxml", "Cargos");
    }

    @FXML
    private void abrirEmpleados() {
        abrir("employee-view.fxml", "Empleados");
    }

    @FXML
    private void salir() {
        if (Mensajes.confirmar(contenedor, "¿Desea cerrar la aplicación?")) {
            Platform.exit();
        }
    }

    private void abrir(String vista, String nombre) {
        try {
            ScreenManager.mostrarVista(FXML + vista);
        } catch (IOException e) {
            Mensajes.mostrar(contenedor, Alert.AlertType.ERROR,
                    "No fue posible abrir " + nombre + ".\n" + e.getMessage());
        }
    }
}
