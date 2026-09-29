package com.uam.facturationapp.controller;

import com.uam.facturationapp.dao.EmployeeDao;
import com.uam.facturationapp.dao.PositionDao;
import com.uam.facturationapp.model.Position;
import com.uam.facturationapp.util.Mensajes;
import com.uam.facturationapp.util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PositionController {
    private final PositionDao positionDao = new PositionDao();
    private final EmployeeDao employeeDao = new EmployeeDao();

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;

    @FXML private TableView<Position> tblCargos;
    @FXML private TableColumn<Position, Integer> colId;
    @FXML private TableColumn<Position, String> colNombre;
    @FXML private TableColumn<Position, String> colDescripcion;

    // Cargo seleccionado en la tabla; null cuando se está registrando uno nuevo.
    private Position seleccionado;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("desc"));

        try {
            tblCargos.setItems(positionDao.findAll());
        } catch (RuntimeException e) {
            Mensajes.mostrar(txtNombre, Alert.AlertType.ERROR, e.getMessage());
        }
        tblCargos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, cargo) -> cargar(cargo));
        nuevo();
    }

    @FXML
    private void nuevo() {
        tblCargos.getSelectionModel().clearSelection();
        cargar(null);
    }

    @FXML
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            Mensajes.mostrar(txtNombre, Alert.AlertType.WARNING, "Ingrese el nombre del cargo.");
            return;
        }

        boolean repetido = tblCargos.getItems().stream()
                .anyMatch(c -> c != seleccionado && c.getName().equalsIgnoreCase(nombre));
        if (repetido) {
            Mensajes.mostrar(txtNombre, Alert.AlertType.WARNING, "Ya existe un cargo con ese nombre.");
            return;
        }

        String descripcion = txtDescripcion.getText().trim();
        try {
            if (seleccionado == null) {
                Position nuevo = new Position(null, nombre, descripcion);
                if (positionDao.save(nuevo)) {
                    tblCargos.getItems().add(nuevo);
                    Mensajes.mostrar(txtNombre, Alert.AlertType.INFORMATION, "Cargo agregado correctamente.");
                }
            } else {
                Position actualizado = new Position(seleccionado.getId(), nombre, descripcion);
                if (positionDao.update(seleccionado.getId(), actualizado)) {
                    seleccionado.setName(nombre);
                    seleccionado.setDesc(descripcion);
                    tblCargos.refresh();
                    Mensajes.mostrar(txtNombre, Alert.AlertType.INFORMATION, "Cargo actualizado correctamente.");
                }
            }
        } catch (RuntimeException e) {
            Mensajes.mostrar(txtNombre, Alert.AlertType.ERROR, e.getMessage());
            return;
        }
        nuevo();
    }

    @FXML
    private void eliminar() {
        if (seleccionado == null) {
            Mensajes.mostrar(txtNombre, Alert.AlertType.WARNING,
                    "Seleccione en la tabla el cargo que desea eliminar.");
            return;
        }
        try {
            if (employeeDao.existeEmpleadoConCargo(seleccionado.getId())) {
                Mensajes.mostrar(txtNombre, Alert.AlertType.WARNING,
                        "No se puede eliminar: hay empleados registrados con este cargo.");
                return;
            }
            if (Mensajes.confirmar(txtNombre, "¿Eliminar el cargo \"" + seleccionado.getName() + "\"?")
                    && positionDao.delete(seleccionado.getId())) {
                tblCargos.getItems().remove(seleccionado);
                nuevo();
            }
        } catch (RuntimeException e) {
            Mensajes.mostrar(txtNombre, Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void cerrar() {
        ScreenManager.mostrarInicio();
    }

    private void cargar(Position cargo) {
        seleccionado = cargo;
        if (cargo == null) {
            txtId.clear();
            txtNombre.clear();
            txtDescripcion.clear();
        } else {
            txtId.setText(String.valueOf(cargo.getId()));
            txtNombre.setText(cargo.getName());
            txtDescripcion.setText(cargo.getDesc());
        }
    }
}
