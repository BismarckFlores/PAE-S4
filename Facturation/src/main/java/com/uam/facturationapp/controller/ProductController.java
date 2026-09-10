package com.uam.facturationapp.controller;

import com.uam.facturationapp.model.Category;
import com.uam.facturationapp.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;

public class ProductController {

    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private CheckBox activeCheck;
    @FXML private ImageView productImage;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> codeColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Category> categoryColumn;
    @FXML private TableColumn<Product, BigDecimal> priceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private TableColumn<Product, Boolean> activeColumn;

    // Lista temporal: se reemplazará por la base de datos cuando se aborde JDBC.
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private String imagePath;

    @FXML
    private void initialize() {
        categoryCombo.setItems(FXCollections.observableArrayList(
                new Category(1, "Alimentos", true),
                new Category(2, "Bebidas", true),
                new Category(3, "Limpieza", true)));

        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        productTable.setItems(products);
        activeCheck.setSelected(true);
    }

    @FXML
    private void onSelectImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        File file = chooser.showOpenDialog(codeField.getScene().getWindow());
        if (file != null) {
            imagePath = file.toURI().toString();
            productImage.setImage(new Image(imagePath));
        }
    }

    @FXML
    private void onSave() {
        if (codeField.getText().isBlank() || nameField.getText().isBlank()
                || priceField.getText().isBlank() || stockField.getText().isBlank()
                || categoryCombo.getValue() == null) {
            showMessage(Alert.AlertType.WARNING, "Complete los campos obligatorios.");
            return;
        }

        BigDecimal price;
        int stock;
        try {
            price = new BigDecimal(priceField.getText().trim());
            stock = Integer.parseInt(stockField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage(Alert.AlertType.ERROR, "Precio o existencia no válidos.");
            return;
        }

        if (price.signum() <= 0 || stock < 0) {
            showMessage(Alert.AlertType.WARNING,
                    "El precio debe ser mayor que cero y la existencia no puede ser negativa.");
            return;
        }

        String code = codeField.getText().trim();
        if (products.stream().anyMatch(p -> p.getCode().equalsIgnoreCase(code))) {
            showMessage(Alert.AlertType.WARNING, "Ya existe un producto con ese código.");
            return;
        }

        products.add(new Product(null, code, nameField.getText().trim(), categoryCombo.getValue(),
                price, stock, imagePath, activeCheck.isSelected()));
        showMessage(Alert.AlertType.INFORMATION, "Producto agregado correctamente.");
        clearForm();
    }

    @FXML
    private void onClose() {
        ((Stage) codeField.getScene().getWindow()).close();
    }

    private void clearForm() {
        codeField.clear();
        nameField.clear();
        priceField.clear();
        stockField.clear();
        categoryCombo.getSelectionModel().clearSelection();
        activeCheck.setSelected(true);
        productImage.setImage(null);
        imagePath = null;
    }

    private void showMessage(Alert.AlertType type, String text) {
        Alert alert = new Alert(type, text, ButtonType.OK);
        alert.initOwner(codeField.getScene().getWindow());
        alert.showAndWait();
    }
}
