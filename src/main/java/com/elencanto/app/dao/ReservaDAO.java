/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.dao;

import com.elencanto.app.model.Reserva;
import com.elencanto.app.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ReservaDAO {
    private static final Logger logger = Logger.getLogger(ReservaDAO.class.getName());

    public boolean crear(Reserva reserva) throws SQLException {
        String sql = "INSERT INTO reservas (habitacion_id, check_in, check_out, total_pagado, estado) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, reserva.getHabitacionId());
            stmt.setTimestamp(2, Timestamp.valueOf(reserva.getCheckIn()));
            stmt.setTimestamp(3, Timestamp.valueOf(reserva.getCheckOut()));
            stmt.setBigDecimal(4, reserva.getTotalPagado());
            stmt.setString(5, reserva.getEstado().name());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reserva.setId(generatedKeys.getLong(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Reserva> obtenerTodas() throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas ORDER BY check_in DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservas.add(mapearReserva(rs));
            }
        }
        
        return reservas;
    }

    public List<Reserva> obtenerReservasActivas() throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE estado = 'ACTIVA' ORDER BY check_in DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservas.add(mapearReserva(rs));
            }
        }
        
        return reservas;
    }

    /**
     * Obtiene todas las reservas realizadas dentro del rango de fechas especificado.
     * 
     * @param inicio Fecha y hora de inicio del rango
     * @param fin Fecha y hora de fin del rango
     * @return Lista de reservas dentro del rango especificado
     * @throws SQLException Si ocurre un error en la consulta a la base de datos
     */
    public List<Reserva> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE check_in BETWEEN ? AND ? ORDER BY check_in DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapearReserva(rs));
                }
            }
        }
        
        return reservas;
    }

    public int contarReservasPorRango(LocalDateTime inicio, LocalDateTime fin) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM reservas WHERE check_in BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public Reserva obtenerPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM reservas WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearReserva(rs);
                }
            }
        }
        return null;
    }

    public boolean actualizarEstado(Long id, Reserva.EstadoReserva estado) throws SQLException {
        String sql = "UPDATE reservas SET estado = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.name());
            stmt.setLong(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }

    private Reserva mapearReserva(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setId(rs.getLong("id"));
        reserva.setHabitacionId(rs.getLong("habitacion_id"));
        reserva.setCheckIn(rs.getTimestamp("check_in").toLocalDateTime());
        reserva.setCheckOut(rs.getTimestamp("check_out").toLocalDateTime());
        reserva.setTotalPagado(rs.getBigDecimal("total_pagado"));
        reserva.setEstado(rs.getString("estado"));
        return reserva;
    }
}