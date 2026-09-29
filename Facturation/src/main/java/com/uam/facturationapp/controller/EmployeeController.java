package com.uam.facturationapp.controller;

import com.uam.facturationapp.dao.EmployeeDao;
import com.uam.facturationapp.dao.PositionDao;
import com.uam.facturationapp.model.Employee;
import com.uam.facturationapp.model.Position;
import com.uam.facturationapp.util.Mensajes;
import com.uam.facturationapp.util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class EmployeeController {
    private final EmployeeDao employeeDao = new EmployeeDao();
    private final PositionDao positionDao = new PositionDao();

    @FXML private TextField txtId;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private ComboBox<Position> cmbCargo;
    @FXML private DatePicker dpFechaContratacion;
    @FXML private CheckBox chkActivo;

    @FXML private TableView<Employee> tblEmpleados;
    @FXML private TableColumn<Employee, Integer> colId;
    @FXML private TableColumn<Employee, String> colNombres;
    @FXML private TableColumn<Employee, String> colApellidos;
    @FXML private TableColumn<Employee, Position> colCargo;
    @FXML private TableColumn<Employee, LocalDate> colFechaContratacion;
    @FXML private TableColumn<Employee, Boolean> colActivo;

    // Empleado seleccionado en la tabla; null cuando se está registrando uno nuevo.
    private Employee seleccionado;

    @FXML
    private void initialize() {
        try {
            cmbCargo.setItems(positionDao.findAll());
        } catch (RuntimeException e) {
            Mensajes.mostrar(txtNombres, Alert.AlertType.ERROR, e.getMessage());
        }

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("names"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("position"));
        colFechaContratacion.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("active"));

        try {
            tblEmpleados.setItems(employeeDao.findAll());
        } catch (RuntimeException e) {
            Mensajes.mostrar(txtNombres, Alert.AlertType.ERROR, e.getMessage());
        }
        tblEmpleados.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, empleado) -> cargar(empleado));
        nuevo();
    }

    @FXML
    private void nuevo() {
        tblEmpleados.getSelectionModel().clearSelection();
        cargar(null);
    }

    @FXML
    private void guardar() {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        Position cargo = cmbCargo.getValue();
        LocalDate fecha = dpFechaContratacion.getValue();

        if (nombres.isEmpty() || apellidos.isEmpty() || cargo == null || fecha == null) {
            Mensajes.mostrar(txtNombres, Alert.AlertType.WARNING, "Complete los campos obligatorios.");
            return;
        }
        if (fecha.isAfter(LocalDate.now())) {
            Mensajes.mostrar(txtNombres, Alert.AlertType.WARNING,
                    "La fecha de contratación no puede ser posterior a hoy.");
            return;
        }

        try {
            if (seleccionado == null) {
                Employee nuevo = new Employee(null, nombres, apellidos, cargo, fecha, chkActivo.isSelected());
                if (employeeDao.save(nuevo)) {
                    tblEmpleados.getItems().add(nuevo);
                    Mensajes.mostrar(txtNombres, Alert.AlertType.INFORMATION, "Empleado agregado correctamente.");
                }
            } else {
                Employee actualizado = new Employee(seleccionado.getId(), nombres, apellidos, cargo,
                        fecha, chkActivo.isSelected());
                if (employeeDao.update(seleccionado.getId(), actualizado)) {
                    seleccionado.setNames(nombres);
                    seleccionado.setLastname(apellidos);
                    seleccionado.setPosition(cargo);
                    seleccionado.setHireDate(fecha);
                    seleccionado.setActive(chkActivo.isSelected());
                    tblEmpleados.refresh();
                    Mensajes.mostrar(txtNombres, Alert.AlertType.INFORMATION, "Empleado actualizado correctamente.");
                }
            }
        } catch (RuntimeException e) {
            Mensajes.mostrar(txtNombres, Alert.AlertType.ERROR, e.getMessage());
            return;
        }
        nuevo();
    }

    @FXML
    private void eliminar() {
        if (seleccionado == null) {
            Mensajes.mostrar(txtNombres, Alert.AlertType.WARNING,
                    "Seleccione en la tabla el empleado que desea eliminar.");
            return;
        }
        String nombre = seleccionado.getNames() + " " + seleccionado.getLastname();
        try {
            if (Mensajes.confirmar(txtNombres, "¿Eliminar al empleado " + nombre + "?")
                    && employeeDao.delete(seleccionado.getId())) {
                tblEmpleados.getItems().remove(seleccionado);
                nuevo();
            }
        } catch (RuntimeException e) {
            Mensajes.mostrar(txtNombres, Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void cerrar() {
        ScreenManager.mostrarInicio();
    }

    private void cargar(Employee empleado) {
        seleccionado = empleado;
        if (empleado == null) {
            txtId.clear();
            txtNombres.clear();
            txtApellidos.clear();
            cmbCargo.getSelectionModel().clearSelection();
            dpFechaContratacion.setValue(null);
            chkActivo.setSelected(true);
        } else {
            txtId.setText(String.valueOf(empleado.getId()));
            txtNombres.setText(empleado.getNames());
            txtApellidos.setText(empleado.getLastname());
            cmbCargo.getItems().stream()
                    .filter(cargo -> cargo.getId().equals(empleado.getPosition().getId()))
                    .findFirst().ifPresentOrElse(cmbCargo::setValue,
                            () -> cmbCargo.setValue(empleado.getPosition()));
            dpFechaContratacion.setValue(empleado.getHireDate());
            chkActivo.setSelected(empleado.isActive());
        }
    }
}
