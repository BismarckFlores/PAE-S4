package com.uam.facturationapp.controller;

import com.uam.facturationapp.dao.CategoryDao;
import com.uam.facturationapp.dao.ProductDao;
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

    private final ProductDao productDao = new ProductDao();
    private final CategoryDao categoryDao = new CategoryDao();

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

    // Producto seleccionado en la tabla; null cuando se está registrando uno nuevo.
    private Product seleccionado;
    private String rutaImagen;

    @FXML
    private void initialize() {
        // Solo se ofrecen las categorías activas.
        cmbCategoria.setItems(new FilteredList<>(categoryDao.findAll(), Category::isActive));

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("code"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("active"));

        try {
            tblProductos.setItems(productDao.findAll());
        } catch (RuntimeException e) {
            mensaje(Alert.AlertType.ERROR, e.getMessage());
        }
        tblProductos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, producto) -> cargar(producto));
        nuevo();
    }

    @FXML
    private void nuevo() {
        tblProductos.getSelectionModel().clearSelection();
        cargar(null);
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
    private void quitarImagen() {
        rutaImagen = null;
        imgProducto.setImage(null);
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
        boolean repetido = tblProductos.getItems().stream()
                .anyMatch(p -> p != seleccionado && p.getCode().equalsIgnoreCase(codigo));
        if (repetido) {
            mensaje(Alert.AlertType.WARNING, "Ya existe un producto con ese código.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        try {
            if (seleccionado == null) {
                Product nuevo = new Product(null, codigo, nombre, cmbCategoria.getValue(),
                        precio, existencia, rutaImagen, chkActivo.isSelected());
                if (productDao.save(nuevo)) {
                    tblProductos.getItems().add(nuevo);
                    mensaje(Alert.AlertType.INFORMATION, "Producto agregado correctamente.");
                }
            } else {
                seleccionado.setCode(codigo);
                seleccionado.setName(nombre);
                seleccionado.setCategory(cmbCategoria.getValue());
                seleccionado.setSalePrice(precio);
                seleccionado.setStock(existencia);
                seleccionado.setImagePath(rutaImagen);
                seleccionado.setActive(chkActivo.isSelected());
                if (productDao.update(seleccionado.getId(), seleccionado)) {
                    tblProductos.refresh();
                    mensaje(Alert.AlertType.INFORMATION, "Producto actualizado correctamente.");
                }
            }
        } catch (RuntimeException e) {
            mensaje(Alert.AlertType.ERROR, e.getMessage());
            return;
        }
        nuevo();
    }

    @FXML
    private void eliminar() {
        if (seleccionado == null) {
            mensaje(Alert.AlertType.WARNING, "Seleccione en la tabla el producto que desea eliminar.");
            return;
        }
        try {
            if (Mensajes.confirmar(txtCodigo, "¿Eliminar el producto \"" + seleccionado.getName() + "\"?")
                    && productDao.delete(seleccionado.getId())) {
                tblProductos.getItems().remove(seleccionado);
                nuevo();
            }
        } catch (RuntimeException e) {
            mensaje(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void cerrar() {
        ScreenManager.mostrarInicio();
    }

    private void cargar(Product producto) {
        seleccionado = producto;
        if (producto == null) {
            txtCodigo.clear();
            txtNombre.clear();
            txtPrecio.clear();
            txtExistencia.clear();
            cmbCategoria.setValue(null);
            chkActivo.setSelected(true);
            rutaImagen = null;
        } else {
            txtCodigo.setText(producto.getCode());
            txtNombre.setText(producto.getName());
            cmbCategoria.setValue(producto.getCategory());
            txtPrecio.setText(producto.getSalePrice().toPlainString());
            txtExistencia.setText(String.valueOf(producto.getStock()));
            chkActivo.setSelected(producto.isActive());
            rutaImagen = producto.getImagePath();
        }
        imgProducto.setImage(rutaImagen == null ? null : new Image(rutaImagen, true));
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        Mensajes.mostrar(txtCodigo, tipo, texto);
    }
}