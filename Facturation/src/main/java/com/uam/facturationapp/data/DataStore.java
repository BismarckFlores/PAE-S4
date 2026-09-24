package com.uam.facturationapp.data;

import com.uam.facturationapp.model.Employee;
import com.uam.facturationapp.model.Position;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.ToIntFunction;

/**
 * Datos en memoria compartidos por todas las vistas, para que la relación
 * empleado → cargo apunte a los mismos objetos.
 * Categorías y productos ya se consultan directamente contra la base de datos
 * a través de CategoryDao y ProductDao.
 */
public final class DataStore {

    private static final ObservableList<Position> cargos = FXCollections.observableArrayList(
            new Position(1, "Cajero", "Registra las ventas y atiende la caja"),
            new Position(2, "Bodeguero", "Controla la existencia de productos"),
            new Position(3, "Administrador", "Supervisa la operación del negocio"));

    private static final ObservableList<Employee> empleados = FXCollections.observableArrayList();

    private DataStore() {
    }

    public static ObservableList<Position> cargos() {
        return cargos;
    }

    public static ObservableList<Employee> empleados() {
        return empleados;
    }

    public static <T> int siguienteId(List<T> lista, ToIntFunction<T> id) {
        return lista.stream().mapToInt(id).max().orElse(0) + 1;
    }

    public static boolean cargoEnUso(Position cargo) {
        return empleados.stream().anyMatch(e -> e.getPosition() == cargo);
    }
}