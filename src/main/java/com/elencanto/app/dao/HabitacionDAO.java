/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.dao;

import com.elencanto.app.model.Habitacion;
import com.elencanto.app.util.DatabaseConnection;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
/**
 *
 * @author Gopar117
 */
public class HabitacionDAO {
    private static final Logger logger = Logger.getLogger(HabitacionDAO.class.getName());
    private Connection connection;
    
    public HabitacionDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public boolean agregarHabitacion(Habitacion habitacion) {
        String sql = "INSERT INTO habitaciones (numero, tipo, estado, tarifa, caracteristicas) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, habitacion.getNumero());
            pstmt.setString(2, habitacion.getTipo().toString());
            pstmt.setString(3, habitacion.getEstado().toString());
            pstmt.setBigDecimal(4, habitacion.getTarifa());
            pstmt.setString(5, habitacion.getCaracteristicas());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    habitacion.setId(generatedKeys.getLong(1));
                }
            }
            
            return true;
        } catch (SQLException e) {
            logger.severe("Error al agregar habitación: " + e.getMessage());
            return false;
        }
    }
    
    public Optional<Habitacion> buscarPorId(Long id) {
        String sql = "SELECT * FROM habitaciones WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToHabitacion(rs));
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al buscar habitación por ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public Optional<Habitacion> buscarPorNumero(String numero) {
        String sql = "SELECT * FROM habitaciones WHERE numero = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, numero);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToHabitacion(rs));
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al buscar habitación: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public List<Habitacion> listarTodas() {
        List<Habitacion> habitaciones = new ArrayList<>();
        String sql = "SELECT * FROM habitaciones ORDER BY numero";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                habitaciones.add(mapResultSetToHabitacion(rs));
            }
        } catch (SQLException e) {
            logger.severe("Error al listar habitaciones: " + e.getMessage());
        }
        
        return habitaciones;
    }
    
    public List<Habitacion> listarPorEstado(Habitacion.EstadoHabitacion estado) {
        List<Habitacion> habitaciones = new ArrayList<>();
        String sql = "SELECT * FROM habitaciones WHERE estado = ? ORDER BY numero";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, estado.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    habitaciones.add(mapResultSetToHabitacion(rs));
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al listar habitaciones por estado: " + e.getMessage());
        }
        
        return habitaciones;
    }
    
    public boolean actualizarHabitacion(Habitacion habitacion) {
        String sql = "UPDATE habitaciones SET numero = ?, tipo = ?, estado = ?, tarifa = ?, caracteristicas = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, habitacion.getNumero());
            pstmt.setString(2, habitacion.getTipo().toString());
            pstmt.setString(3, habitacion.getEstado().toString());
            pstmt.setBigDecimal(4, habitacion.getTarifa());
            pstmt.setString(5, habitacion.getCaracteristicas());
            pstmt.setLong(6, habitacion.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.severe("Error al actualizar habitación: " + e.getMessage());
            return false;
        }
    }
    
    public int contarTotalHabitaciones() {
        String sql = "SELECT COUNT(*) FROM habitaciones";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.severe("Error al contar habitaciones: " + e.getMessage());
        }
        
        return 0;
    }
    
    public int contarHabitacionesPorEstado(Habitacion.EstadoHabitacion estado) {
        String sql = "SELECT COUNT(*) FROM habitaciones WHERE estado = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, estado.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al contar habitaciones por estado: " + e.getMessage());
        }
        
        return 0;
    }
    
    private Habitacion mapResultSetToHabitacion(ResultSet rs) throws SQLException {
        Habitacion habitacion = new Habitacion();
        habitacion.setId(rs.getLong("id"));
        habitacion.setNumero(rs.getString("numero"));
        habitacion.setTipo(Habitacion.TipoHabitacion.valueOf(rs.getString("tipo")));
        habitacion.setEstado(Habitacion.EstadoHabitacion.valueOf(rs.getString("estado")));
        habitacion.setTarifa(rs.getBigDecimal("tarifa"));
        habitacion.setCaracteristicas(rs.getString("caracteristicas"));
        return habitacion;
    }
}
