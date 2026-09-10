package com.uam.facturationapp.controller;

import com.uam.facturationapp.data.DataStore;
import com.uam.facturationapp.model.Employee;
import com.uam.facturationapp.model.Position;
import com.uam.facturationapp.util.Mensajes;
import com.uam.facturationapp.util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class EmployeeController {

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
        // Los cargos son los registrados en la vista Cargos.
        cmbCargo.setItems(DataStore.cargos());

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("names"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("position"));
        colFechaContratacion.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("active"));

        tblEmpleados.setItems(DataStore.empleados());
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

        if (seleccionado == null) {
            DataStore.empleados().add(new Employee(
                    DataStore.siguienteId(DataStore.empleados(), Employee::getId),
                    nombres, apellidos, cargo, fecha, chkActivo.isSelected()));
            Mensajes.mostrar(txtNombres, Alert.AlertType.INFORMATION, "Empleado agregado correctamente.");
        } else {
            seleccionado.setNames(nombres);
            seleccionado.setLastname(apellidos);
            seleccionado.setPosition(cargo);
            seleccionado.setHireDate(fecha);
            seleccionado.setActive(chkActivo.isSelected());
            tblEmpleados.refresh();
            Mensajes.mostrar(txtNombres, Alert.AlertType.INFORMATION, "Empleado actualizado correctamente.");
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
        if (Mensajes.confirmar(txtNombres, "¿Eliminar al empleado " + nombre + "?")) {
            DataStore.empleados().remove(seleccionado);
            nuevo();
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
            cmbCargo.setValue(empleado.getPosition());
            dpFechaContratacion.setValue(empleado.getHireDate());
            chkActivo.setSelected(empleado.isActive());
        }
    }
}
