package com.uam.facturationapp.controller;

import com.uam.facturationapp.dao.CategoryDao;
import com.uam.facturationapp.dao.ProductDao;
import com.uam.facturationapp.data.DataStore;
import com.uam.facturationapp.util.Mensajes;
import com.uam.facturationapp.util.ScreenManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainMenuController {
    private final CategoryDao categoryDao = new CategoryDao();
    private final ProductDao productDao = new ProductDao();

    private static final String FXML = "/com/uam/facturationapp/fxml/";

    @FXML private BorderPane contenedor;

    @FXML private Label lblTotalCategorias;
    @FXML private Label lblTotalProductos;
    @FXML private Label lblTotalCargos;
    @FXML private Label lblTotalEmpleados;

    @FXML
    private void initialize() {
        ScreenManager.setContenedor(contenedor);

        try {
            mostrarTotal(lblTotalCategorias, categoryDao.findAll().size(),
                    "categoría registrada", "categorías registradas");
            mostrarTotal(lblTotalProductos, productDao.findAll().size(),
                    "producto registrado", "productos registrados");
        } catch (RuntimeException e) {
            Mensajes.mostrar(contenedor, Alert.AlertType.ERROR, e.getMessage());
        }
        // Cargos y empleados siguen en memoria: se enlazan para reflejar cambios sin recargar la vista.
        mostrarTotal(lblTotalCargos, DataStore.cargos(), "cargo registrado", "cargos registrados");
        mostrarTotal(lblTotalEmpleados, DataStore.empleados(), "empleado registrado", "empleados registrados");
    }

    @FXML
    private void irInicio() {
        ScreenManager.mostrarInicio();
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

    private static void mostrarTotal(Label etiqueta, ObservableList<?> lista, String singular, String plural) {
        etiqueta.textProperty().bind(Bindings.createStringBinding(
                () -> lista.size() + " " + (lista.size() == 1 ? singular : plural), lista));
    }

    private static void mostrarTotal(Label etiqueta, int total, String singular, String plural) {
        etiqueta.setText(total + " " + (total == 1 ? singular : plural));
    }
}
