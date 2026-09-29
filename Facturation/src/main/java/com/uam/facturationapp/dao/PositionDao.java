package com.uam.facturationapp.dao;

import com.uam.facturationapp.interfaces.Dao;
import com.uam.facturationapp.model.Position;
import com.uam.facturationapp.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class PositionDao implements Dao<Position, Integer> {
    @Override
    public ObservableList<Position> findAll() {
        ObservableList<Position> cargos = FXCollections.observableArrayList();
        String sql = "SELECT id, nombre, descripcion FROM cargo ORDER BY id";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cargos.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar los cargos.", e);
        }
        return cargos;
    }

    @Override
    public Optional<Position> findById(Integer id) {
        String sql = "SELECT id, nombre, descripcion FROM cargo WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el cargo.", e);
        }
    }

    @Override
    public boolean save(Position entity) {
        String sql = "INSERT INTO cargo (nombre, descripcion) VALUES (?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDesc());
            if (ps.executeUpdate() == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entity.setId(rs.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el cargo.", e);
        }
    }

    @Override
    public boolean update(Integer id, Position entity) {
        String sql = "UPDATE cargo SET nombre = ?, descripcion = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDesc());
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el cargo.", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM cargo WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el cargo.", e);
        }
    }

    private Position mapear(ResultSet rs) throws SQLException {
        return new Position(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion"));
    }
}
