package com.uam.facturationapp.controller;

import com.uam.facturationapp.data.DataStore;
import com.uam.facturationapp.model.Category;
import com.uam.facturationapp.model.Product;
import com.uam.facturationapp.util.Mensajes;
import com.uam.facturationapp.util.ScreenManager;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;

public class ProductController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Category> cmbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;

    @FXML private TableView<Product> tblProductos;
    @FXML private TableColumn<Product, String> colCodigo;
    @FXML private TableColumn<Product, String> colNombre;
    @FXML private TableColumn<Product, Category> colCategoria;
    @FXML private TableColumn<Product, BigDecimal> colPrecio;
    @FXML private TableColumn<Product, Integer> colExistencia;
    @FXML private TableColumn<Product, Boolean> colActivo;

    private String rutaImagen;

    @FXML
    private void initialize() {
        // Solo se ofrecen las categorías activas registradas en la vista Categorías.
        cmbCategoria.setItems(new FilteredList<>(DataStore.categorias(), Category::isActive));

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("code"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("active"));

        tblProductos.setItems(DataStore.productos());
        chkActivo.setSelected(true);
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        File archivo = chooser.showOpenDialog(txtCodigo.getScene().getWindow());
        if (archivo != null) {
            rutaImagen = archivo.toURI().toString();
            imgProducto.setImage(new Image(rutaImagen));
        }
    }

    @FXML
    private void guardar() {
        if (txtCodigo.getText().isBlank() || txtNombre.getText().isBlank()
                || txtPrecio.getText().isBlank() || txtExistencia.getText().isBlank()
                || cmbCategoria.getValue() == null) {
            mensaje(Alert.AlertType.WARNING, "Complete los campos obligatorios.");
            return;
        }

        BigDecimal precio;
        int existencia;
        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
            existencia = Integer.parseInt(txtExistencia.getText().trim());
        } catch (NumberFormatException e) {
            mensaje(Alert.AlertType.ERROR, "Precio o existencia no válidos.");
            return;
        }

        if (precio.signum() <= 0 || existencia < 0) {
            mensaje(Alert.AlertType.WARNING,
                    "El precio debe ser mayor que cero y la existencia no puede ser negativa.");
            return;
        }

        String codigo = txtCodigo.getText().trim();
        if (DataStore.productos().stream().anyMatch(p -> p.getCode().equalsIgnoreCase(codigo))) {
            mensaje(Alert.AlertType.WARNING, "Ya existe un producto con ese código.");
            return;
        }

        DataStore.productos().add(new Product(DataStore.siguienteId(DataStore.productos(), Product::getId),
                codigo, txtNombre.getText().trim(), cmbCategoria.getValue(),
                precio, existencia, rutaImagen, chkActivo.isSelected()));
        mensaje(Alert.AlertType.INFORMATION, "Producto agregado correctamente.");
        limpiar();
    }

    @FXML
    private void cerrar() {
        ScreenManager.mostrarInicio();
    }

    private void limpiar() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        chkActivo.setSelected(true);
        imgProducto.setImage(null);
        rutaImagen = null;
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        Mensajes.mostrar(txtCodigo, tipo, texto);
    }
}
