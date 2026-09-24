package com.uam.facturationapp.dao;

import com.uam.facturationapp.interfaces.Dao;
import com.uam.facturationapp.model.Category;
import com.uam.facturationapp.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class CategoryDao implements Dao<Category, Integer> {
    @Override
    public ObservableList<Category> findAll() {
        ObservableList<Category> categorias = FXCollections.observableArrayList();
        String sql = "SELECT id, nombre, activa FROM categoria ORDER BY id";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categorias.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar las categorías.", e);
        }
        return categorias;
    }

    @Override
    public Optional<Category> findById(Integer id) {
        String sql = "SELECT id, nombre, activa FROM categoria WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la categoría.", e);
        }
    }

    @Override
    public boolean save(Category entity) {
        String sql = "INSERT INTO categoria (nombre, activa) VALUES (?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getName());
            ps.setBoolean(2, entity.isActive());
            if (ps.executeUpdate() == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entity.setId(rs.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la categoría.", e);
        }
    }
    @Override
    public boolean update(Integer id, Category entity) {
        String sql = "UPDATE categoria SET nombre = ?, activa = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entity.getName());
            ps.setBoolean(2, entity.isActive());
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la categoría.", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la categoría.", e);
        }
    }

    private Category mapear(ResultSet rs) throws SQLException {
        return new Category(rs.getInt("id"), rs.getString("nombre"), rs.getBoolean("activa"));
    }

}
