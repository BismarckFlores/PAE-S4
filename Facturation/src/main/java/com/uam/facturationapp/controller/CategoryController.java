package com.uam.facturationapp.controller;

import com.uam.facturationapp.model.Category;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CategoryController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private CheckBox activeCheck;

    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;

    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Integer> idColumn;
    @FXML private TableColumn<Category, String> nameColumn;
    @FXML private TableColumn<Category, Boolean> activeColumn;

    @FXML
    private void initialize() {
        // Aún sin lógica: solo diseño.
    }

    @FXML
    private void onNew() {
    }

    @FXML
    private void onSave() {
    }

    @FXML
    private void onDelete() {
    }
}
