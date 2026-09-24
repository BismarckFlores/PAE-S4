package com.uam.facturationapp.dao;

import com.uam.facturationapp.interfaces.Dao;
import com.uam.facturationapp.model.Category;
import com.uam.facturationapp.model.Product;
import com.uam.facturationapp.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class ProductDao implements Dao<Product, Integer> {

    private static final String SELECT_BASE =
            "SELECT p.id, p.codigo, p.nombre, p.precio_venta, p.existencia, p.ruta_imagen, p.activo, "
                    + "c.id AS categoria_id, c.nombre AS categoria_nombre, c.activa AS categoria_activa "
                    + "FROM producto p JOIN categoria c ON c.id = p.categoria_id";

    @Override
    public ObservableList<Product> findAll() {
        ObservableList<Product> productos = FXCollections.observableArrayList();
        String sql = SELECT_BASE + " ORDER BY p.id";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar los productos.", e);
        }
        return productos;
    }

    @Override
    public Optional<Product> findById(Integer id) {
        String sql = SELECT_BASE + " WHERE p.id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el producto.", e);
        }
    }

    @Override
    public boolean save(Product entity) {
        String sql = "INSERT INTO producto (codigo, nombre, categoria_id, precio_venta, existencia, ruta_imagen, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            llenarParametros(ps, entity);
            if (ps.executeUpdate() == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entity.setId(rs.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el producto.", e);
        }
    }

    @Override
    public boolean update(Integer id, Product entity) {
        String sql = "UPDATE producto SET codigo = ?, nombre = ?, categoria_id = ?, precio_venta = ?, "
                + "existencia = ?, ruta_imagen = ?, activo = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            llenarParametros(ps, entity);
            ps.setInt(8, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el producto.", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM producto WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el producto.", e);
        }
    }

    public boolean existeProductoConCategoria(Integer categoriaId) {
        String sql = "SELECT 1 FROM producto WHERE categoria_id = ? LIMIT 1";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar el uso de la categoría.", e);
        }
    }

    private void llenarParametros(PreparedStatement ps, Product entity) throws SQLException {
        ps.setString(1, entity.getCode());
        ps.setString(2, entity.getName());
        ps.setInt(3, entity.getCategory().getId());
        ps.setBigDecimal(4, entity.getSalePrice());
        ps.setInt(5, entity.getStock());
        ps.setString(6, entity.getImagePath());
        ps.setBoolean(7, entity.isActive());
    }

    private Product mapear(ResultSet rs) throws SQLException {
        Category categoria = new Category(rs.getInt("categoria_id"), rs.getString("categoria_nombre"),
                rs.getBoolean("categoria_activa"));
        return new Product(rs.getInt("id"), rs.getString("codigo"), rs.getString("nombre"), categoria,
                rs.getBigDecimal("precio_venta"), rs.getInt("existencia"), rs.getString("ruta_imagen"),
                rs.getBoolean("activo"));
    }
}