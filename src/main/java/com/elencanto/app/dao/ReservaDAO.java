/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.dao;

import com.elencanto.app.model.Habitacion;
import com.elencanto.app.model.Reserva;
import com.elencanto.app.util.DatabaseConnection;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
/**
 *
 * @author Gopar117
 */
public class ReservaDAO {
    private static final Logger logger = Logger.getLogger(ReservaDAO.class.getName());
    private Connection connection;
    private HabitacionDAO habitacionDAO;
    
    public ReservaDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.habitacionDAO = new HabitacionDAO();
    }
    
    public boolean agregarReserva(Reserva reserva) {
        String sql = "INSERT INTO reservas (habitacion_id, check_in, check_out, total_pagado, estado) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, reserva.getHabitacion().getId());
            pstmt.setTimestamp(2, Timestamp.valueOf(reserva.getCheckIn()));
            pstmt.setTimestamp(3, Timestamp.valueOf(reserva.getCheckOut()));
            pstmt.setBigDecimal(4, reserva.getTotalPagado());
            pstmt.setString(5, reserva.getEstado().toString());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reserva.setId(generatedKeys.getLong(1));
                }
            }
            
            return true;
        } catch (SQLException e) {
            logger.severe("Error al agregar reserva: " + e.getMessage());
            return false;
        }
    }
    
    public Optional<Reserva> buscarPorId(Long id) {
        String sql = "SELECT * FROM reservas WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReserva(rs));
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al buscar reserva: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public List<Reserva> listarTodas() {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas ORDER BY check_in DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservas.add(mapResultSetToReserva(rs));
            }
        } catch (SQLException e) {
            logger.severe("Error al listar reservas: " + e.getMessage());
        }
        
        return reservas;
    }
    
    public List<Reserva> listarPorEstado(Reserva.EstadoReserva estado) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE estado = ? ORDER BY check_in DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, estado.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapResultSetToReserva(rs));
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al listar reservas por estado: " + e.getMessage());
        }
        
        return reservas;
    }
    
    public List<Reserva> listarPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE check_in BETWEEN ? AND ? ORDER BY check_in";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(inicio));
            pstmt.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapResultSetToReserva(rs));
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al listar reservas por fecha: " + e.getMessage());
        }
        
        return reservas;
    }
    
    public boolean actualizarReserva(Reserva reserva) {
        String sql = "UPDATE reservas SET habitacion_id = ?, check_in = ?, check_out = ?, " +
                    "total_pagado = ?, estado = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, reserva.getHabitacion().getId());
            pstmt.setTimestamp(2, Timestamp.valueOf(reserva.getCheckIn()));
            pstmt.setTimestamp(3, Timestamp.valueOf(reserva.getCheckOut()));
            pstmt.setBigDecimal(4, reserva.getTotalPagado());
            pstmt.setString(5, reserva.getEstado().toString());
            pstmt.setLong(6, reserva.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.severe("Error al actualizar reserva: " + e.getMessage());
            return false;
        }
    }
    
    public BigDecimal calcularIngresosPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        String sql = "SELECT SUM(total_pagado) FROM reservas WHERE check_in BETWEEN ? AND ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(inicio));
            pstmt.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal resultado = rs.getBigDecimal(1);
                    return resultado != null ? resultado : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al calcular ingresos: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    public int contarReservasPorRango(LocalDateTime inicio, LocalDateTime fin) {
        String sql = "SELECT COUNT(*) FROM reservas WHERE check_in BETWEEN ? AND ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(inicio));
            pstmt.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al contar reservas por rango: " + e.getMessage());
        }
        
        return 0;
    }
    
    private Reserva mapResultSetToReserva(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setId(rs.getLong("id"));
        
        // Obtener habitación
        Long habitacionId = rs.getLong("habitacion_id");
        Optional<Habitacion> habitacionOpt = habitacionDAO.buscarPorId(habitacionId);
        
        if (habitacionOpt.isPresent()) {
            reserva.setHabitacion(habitacionOpt.get());
        } else {
            logger.warning("No se encontró habitación con ID " + habitacionId);
        }
        
        reserva.setCheckIn(rs.getTimestamp("check_in").toLocalDateTime());
        reserva.setCheckOut(rs.getTimestamp("check_out").toLocalDateTime());
        reserva.setTotalPagado(rs.getBigDecimal("total_pagado"));
        reserva.setEstado(Reserva.EstadoReserva.valueOf(rs.getString("estado")));
        
        return reserva;
    }
}