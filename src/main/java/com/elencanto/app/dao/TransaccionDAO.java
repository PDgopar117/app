/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.dao;

import com.elencanto.app.model.Transaccion;
import com.elencanto.app.model.Transaccion.TipoTransaccion;
import com.elencanto.app.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.math.BigDecimal;

public class TransaccionDAO {
    private static final Logger logger = Logger.getLogger(TransaccionDAO.class.getName());
    private final UsuarioDAO usuarioDAO;

    public TransaccionDAO() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public boolean crear(Transaccion transaccion) throws SQLException {
        String sql = "INSERT INTO transacciones (tipo, concepto, monto, fecha, usuario_id, reserva_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, transaccion.getTipo().name());
            stmt.setString(2, transaccion.getConcepto());
            stmt.setBigDecimal(3, transaccion.getMonto());
            stmt.setTimestamp(4, Timestamp.valueOf(transaccion.getFecha()));
            stmt.setLong(5, transaccion.getUsuarioId());
            if (transaccion.getReservaId() != null) {
                stmt.setLong(6, transaccion.getReservaId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaccion.setId(generatedKeys.getLong(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Transaccion> obtenerTodas() throws SQLException {
        List<Transaccion> transacciones = new ArrayList<>();
        String sql = "SELECT * FROM transacciones ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transacciones.add(mapearTransaccion(rs));
            }
        }
        
        return transacciones;
    }

    public List<Transaccion> obtenerPorFecha(Date fecha) throws SQLException {
        List<Transaccion> transacciones = new ArrayList<>();
        String sql = "SELECT * FROM transacciones WHERE DATE(fecha) = ? ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, fecha);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacciones.add(mapearTransaccion(rs));
                }
            }
        }
        
        return transacciones;
    }

    /**
     * Obtiene todas las transacciones realizadas dentro del rango de fechas especificado.
     * 
     * @param inicio Fecha y hora de inicio del rango
     * @param fin Fecha y hora de fin del rango
     * @return Lista de transacciones dentro del rango especificado
     * @throws SQLException Si ocurre un error en la consulta a la base de datos
     */
    public List<Transaccion> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) throws SQLException {
        List<Transaccion> transacciones = new ArrayList<>();
        String sql = "SELECT * FROM transacciones WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacciones.add(mapearTransaccion(rs));
                }
            }
        }
        
        return transacciones;
    }

    public BigDecimal calcularTotalPorTipoYFecha(TipoTransaccion tipo, LocalDate fecha) throws SQLException {
        String sql = "SELECT SUM(monto) as total FROM transacciones WHERE tipo = ? AND DATE(fecha) = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo.name());
            stmt.setDate(2, Date.valueOf(fecha));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calcularTotalPorTipoYRango(TipoTransaccion tipo, LocalDateTime inicio, LocalDateTime fin) throws SQLException {
        String sql = "SELECT SUM(monto) as total FROM transacciones WHERE tipo = ? AND fecha BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo.name());
            stmt.setTimestamp(2, Timestamp.valueOf(inicio));
            stmt.setTimestamp(3, Timestamp.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public List<Transaccion> listarPorTipoYFecha(TipoTransaccion tipo, LocalDate fecha) throws SQLException {
        List<Transaccion> transacciones = new ArrayList<>();
        String sql = "SELECT * FROM transacciones WHERE tipo = ? AND DATE(fecha) = ? ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo.name());
            stmt.setDate(2, Date.valueOf(fecha));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacciones.add(mapearTransaccion(rs));
                }
            }
        }
        
        return transacciones;
    }

    private Transaccion mapearTransaccion(ResultSet rs) throws SQLException {
        Transaccion transaccion = new Transaccion();
        transaccion.setId(rs.getLong("id"));
        transaccion.setTipo(TipoTransaccion.valueOf(rs.getString("tipo")));
        transaccion.setConcepto(rs.getString("concepto"));
        transaccion.setMonto(rs.getBigDecimal("monto"));
        transaccion.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        transaccion.setUsuarioId(rs.getLong("usuario_id"));
        
        Long reservaId = rs.getLong("reserva_id");
        if (!rs.wasNull()) {
            transaccion.setReservaId(reservaId);
        }
        
        return transaccion;
    }

    public boolean agregarTransaccion(Transaccion transaccion) throws SQLException {
        return crear(transaccion);
    }

    public void procesarTransaccion(Transaccion transaccion) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // Verificar usuario
            if (usuarioDAO.buscarPorId(transaccion.getUsuarioId()) == null) {
                throw new SQLException("Usuario no encontrado");
            }

            // Crear la transacción
            if (!crear(transaccion)) {
                throw new SQLException("No se pudo crear la transacción");
            }

            // Si es una transacción relacionada con una reserva, actualizar la reserva
            if (transaccion.getReservaId() != null) {
                String updateReservaSql = "UPDATE reservas SET total_pagado = total_pagado + ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateReservaSql)) {
                    stmt.setBigDecimal(1, transaccion.getMonto());
                    stmt.setLong(2, transaccion.getReservaId());
                    
                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException("No se pudo actualizar la reserva");
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.severe("Error al hacer rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    logger.severe("Error al cerrar conexión: " + ex.getMessage());
                }
            }
        }
    }
}