package org.kinalquickmart.system.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.kinalquickmart.system.model.Employee;

public class EmployeeDAO {

    private Connection getConnection() throws SQLException {
        return ConexionDB.getInstanciaConexionDB().getConnection();
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre_completo, correo, rol, activo FROM Usuario";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getInt("id_usuario"));
                emp.setFullName(rs.getString("nombre_completo"));
                emp.setEmail(rs.getString("correo"));
                emp.setRole(rs.getString("rol"));
                emp.setActive(rs.getBoolean("activo"));
                employees.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public List<Employee> searchEmployees(String texto) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre_completo, correo, rol, activo FROM Usuario WHERE nombre_completo LIKE ? OR correo LIKE ? OR rol LIKE ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String patron = "%" + texto + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employee emp = new Employee();
                    emp.setId(rs.getInt("id_usuario"));
                    emp.setFullName(rs.getString("nombre_completo"));
                    emp.setEmail(rs.getString("correo"));
                    emp.setRole(rs.getString("rol"));
                    emp.setActive(rs.getBoolean("activo"));
                    employees.add(emp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public boolean saveEmployee(Employee employee) {
        String sql = "INSERT INTO Usuario (nombre_completo, correo, password, rol, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getPassword());
            ps.setString(4, employee.getRole());
            ps.setBoolean(5, employee.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE Usuario SET nombre_completo = ?, correo = ?, password = ?, rol = ? WHERE id_usuario = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getPassword());
            ps.setString(4, employee.getRole());
            ps.setInt(5, employee.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEmployeeStatus(Employee employee) {
        String sql = "UPDATE Usuario SET activo = ? WHERE id_usuario = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, employee.isActive());
            ps.setInt(2, employee.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM Usuario WHERE id_usuario = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Usuario WHERE correo = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Employee getEmployeeByEmail(String email) {
        String sql = "SELECT id_usuario, nombre_completo, correo, password, rol, activo FROM Usuario WHERE correo = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Employee emp = new Employee();
                    emp.setId(rs.getInt("id_usuario"));
                    emp.setFullName(rs.getString("nombre_completo"));
                    emp.setEmail(rs.getString("correo"));
                    emp.setPassword(rs.getString("password"));
                    emp.setRole(rs.getString("rol"));
                    emp.setActive(rs.getBoolean("activo"));
                    return emp;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
