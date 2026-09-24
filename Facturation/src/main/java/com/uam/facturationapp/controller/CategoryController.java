package com.uam.facturationapp.controller;

import com.uam.facturationapp.dao.CategoryDao;
import com.uam.facturationapp.dao.ProductDao;
import com.uam.facturationapp.model.Category;
import com.uam.facturationapp.util.Mensajes;
import com.uam.facturationapp.util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CategoryController {

    private final CategoryDao categoryDao = new CategoryDao();
    private final ProductDao productDao = new ProductDao();

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private CheckBox chkActivo;

    @FXML private TableView<Category> tblCategorias;
    @FXML private TableColumn<Category, Integer> colId;
    @FXML private TableColumn<Category, String> colNombre;
    @FXML private TableColumn<Category, Boolean> colActivo;

    // Categoría seleccionada en la tabla; null cuando se está registrando una nueva.
    private Category seleccionada;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("active"));

        try {
            tblCategorias.setItems(categoryDao.findAll());
        } catch (RuntimeException e) {
            Mensajes.mostrar(txtNombre, Alert.AlertType.ERROR, e.getMessage());
        }
        tblCategorias.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, categoria) -> cargar(categoria));
        nuevo();
    }

    @FXML
    private void nuevo() {
        tblCategorias.getSelectionModel().clearSelection();
        cargar(null);
    }

    @FXML
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            Mensajes.mostrar(txtNombre, Alert.AlertType.WARNING, "Ingrese el nombre de la categoría.");
            return;
        }

        boolean repetida = tblCategorias.getItems().stream()
                .anyMatch(c -> c != seleccionada && c.getName().equalsIgnoreCase(nombre));
        if (repetida) {
            Mensajes.mostrar(txtNombre, Alert.AlertType.WARNING, "Ya existe una categoría con ese nombre.");
            return;
        }

        try {
            if (seleccionada == null) {
                Category nueva = new Category(null, nombre, chkActivo.isSelected());
                if (categoryDao.save(nueva)) {
                    tblCategorias.getItems().add(nueva);
                    Mensajes.mostrar(txtNombre, Alert.AlertType.INFORMATION, "Categoría agregada correctamente.");
                }
            } else {
                seleccionada.setName(nombre);
                seleccionada.setActive(chkActivo.isSelected());
                if (categoryDao.update(seleccionada.getId(), seleccionada)) {
                    tblCategorias.refresh();
                    Mensajes.mostrar(txtNombre, Alert.AlertType.INFORMATION, "Categoría actualizada correctamente.");
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
        if (seleccionada == null) {
            Mensajes.mostrar(txtNombre, Alert.AlertType.WARNING,
                    "Seleccione en la tabla la categoría que desea eliminar.");
            return;
        }
        try {
            if (productDao.existeProductoConCategoria(seleccionada.getId())) {
                Mensajes.mostrar(txtNombre, Alert.AlertType.WARNING,
                        "No se puede eliminar: hay productos registrados con esta categoría.\n"
                                + "Puede desactivarla en su lugar.");
                return;
            }
            if (Mensajes.confirmar(txtNombre, "¿Eliminar la categoría \"" + seleccionada.getName() + "\"?")
                    && categoryDao.delete(seleccionada.getId())) {
                tblCategorias.getItems().remove(seleccionada);
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

    private void cargar(Category categoria) {
        seleccionada = categoria;
        if (categoria == null) {
            txtId.clear();
            txtNombre.clear();
            chkActivo.setSelected(true);
        } else {
            txtId.setText(String.valueOf(categoria.getId()));
            txtNombre.setText(categoria.getName());
            chkActivo.setSelected(categoria.isActive());
        }
    }
}