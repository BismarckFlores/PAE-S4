package com.uam.facturationapp.data;

import com.uam.facturationapp.model.Category;
import com.uam.facturationapp.model.Employee;
import com.uam.facturationapp.model.Position;
import com.uam.facturationapp.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.ToIntFunction;

/**
 * Datos en memoria compartidos por todas las vistas, para que las relaciones
 * (producto → categoría, empleado → cargo) apunten a los mismos objetos.
 * Se reemplazará por la base de datos cuando se aborde JDBC.
 */
public final class DataStore {

    private static final ObservableList<Category> categorias = FXCollections.observableArrayList(
            new Category(1, "Alimentos", true),
            new Category(2, "Bebidas", true),
            new Category(3, "Limpieza", true));

    private static final ObservableList<Position> cargos = FXCollections.observableArrayList(
            new Position(1, "Cajero", "Registra las ventas y atiende la caja"),
            new Position(2, "Bodeguero", "Controla la existencia de productos"),
            new Position(3, "Administrador", "Supervisa la operación del negocio"));

    private static final ObservableList<Product> productos = FXCollections.observableArrayList();
    private static final ObservableList<Employee> empleados = FXCollections.observableArrayList();

    private DataStore() {
    }

    public static ObservableList<Category> categorias() {
        return categorias;
    }

    public static ObservableList<Position> cargos() {
        return cargos;
    }

    public static ObservableList<Product> productos() {
        return productos;
    }

    public static ObservableList<Employee> empleados() {
        return empleados;
    }

    public static <T> int siguienteId(List<T> lista, ToIntFunction<T> id) {
        return lista.stream().mapToInt(id).max().orElse(0) + 1;
    }

    public static boolean categoriaEnUso(Category categoria) {
        return productos.stream().anyMatch(p -> p.getCategory() == categoria);
    }

    public static boolean cargoEnUso(Position cargo) {
        return empleados.stream().anyMatch(e -> e.getPosition() == cargo);
    }
}
