package com.uam.facturationapp.dao;

import com.uam.facturationapp.interfaces.Dao;
import com.uam.facturationapp.model.Employee;
import com.uam.facturationapp.model.Position;
import com.uam.facturationapp.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class EmployeeDao implements Dao<Employee, Integer> {
    private static final String SELECT_BASE =
            "SELECT e.id, e.nombres, e.apellidos, e.fecha_contratacion, e.activo, "
                    + "c.id AS cargo_id, c.nombre AS cargo_nombre, c.descripcion AS cargo_descripcion "
                    + "FROM empleado e JOIN cargo c ON c.id = e.cargo_id";

    @Override
    public ObservableList<Employee> findAll() {
        ObservableList<Employee> empleados = FXCollections.observableArrayList();
        String sql = SELECT_BASE + " ORDER BY e.id";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) empleados.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar los empleados.", e);
        }
        return empleados;
    }

    @Override
    public Optional<Employee> findById(Integer id) {
        String sql = SELECT_BASE + " WHERE e.id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el empleado.", e);
        }
    }

    @Override
    public boolean save(Employee entity) {
        String sql = "INSERT INTO empleado (nombres, apellidos, cargo_id, fecha_contratacion, activo) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            llenarParametros(ps, entity);
            if (ps.executeUpdate() == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entity.setId(rs.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el empleado.", e);
        }
    }

    @Override
    public boolean update(Integer id, Employee entity) {
        String sql = "UPDATE empleado SET nombres = ?, apellidos = ?, cargo_id = ?, "
                + "fecha_contratacion = ?, activo = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            llenarParametros(ps, entity);
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el empleado.", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM empleado WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el empleado.", e);
        }
    }

    public boolean existeEmpleadoConCargo(Integer cargoId) {
        String sql = "SELECT 1 FROM empleado WHERE cargo_id = ? LIMIT 1";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cargoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar el uso del cargo.", e);
        }
    }

    private void llenarParametros(PreparedStatement ps, Employee entity) throws SQLException {
        ps.setString(1, entity.getNames());
        ps.setString(2, entity.getLastname());
        ps.setInt(3, entity.getPosition().getId());
        ps.setDate(4, Date.valueOf(entity.getHireDate()));
        ps.setBoolean(5, entity.isActive());
    }

    private Employee mapear(ResultSet rs) throws SQLException {
        Position cargo = new Position(rs.getInt("cargo_id"), rs.getString("cargo_nombre"),
                rs.getString("cargo_descripcion"));
        return new Employee(rs.getInt("id"), rs.getString("nombres"), rs.getString("apellidos"),
                cargo, rs.getDate("fecha_contratacion").toLocalDate(), rs.getBoolean("activo"));
    }
}
