/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.dao;

import com.elencanto.app.model.Reserva;
import com.elencanto.app.model.Transaccion;
import com.elencanto.app.model.Usuario;
import com.elencanto.app.util.DatabaseConnection;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 *
 * @author Gopar117
 */
public class TransaccionDAO {
    private static final Logger logger = Logger.getLogger(TransaccionDAO.class.getName());
    private Connection connection;
    private UsuarioDAO usuarioDAO;
    private ReservaDAO reservaDAO;
    
    public TransaccionDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.usuarioDAO = new UsuarioDAO();
        this.reservaDAO = new ReservaDAO();
    }
    
    public boolean agregarTransaccion(Transaccion transaccion) {
        String sql = "INSERT INTO transacciones (tipo, concepto, monto, fecha, usuario_id, reserva_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, transaccion.getTipo().toString());
            pstmt.setString(2, transaccion.getConcepto());
            pstmt.setBigDecimal(3, transaccion.getMonto());
            pstmt.setTimestamp(4, Timestamp.valueOf(transaccion.getFecha()));
            pstmt.setLong(5, transaccion.getUsuario().getId());
            
            if (transaccion.getReserva() != null) {
                pstmt.setLong(6, transaccion.getReserva().getId());
            } else {
                pstmt.setNull(6, Types.BIGINT);
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaccion.setId(generatedKeys.getLong(1));
                }
            }
            
            return true;
        } catch (SQLException e) {
            logger.severe("Error al agregar transacción: " + e.getMessage());
            return false;
        }
    }
    
    public List<Transaccion> listarPorFecha(LocalDate fecha) {
        List<Transaccion> transacciones = new ArrayList<>();
        
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(23, 59, 59);
        
        String sql = "SELECT * FROM transacciones WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(inicioDia));
            pstmt.setTimestamp(2, Timestamp.valueOf(finDia));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transacciones.add(mapResultSetToTransaccion(rs));
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al listar transacciones por fecha: " + e.getMessage());
        }
        
        return transacciones;
    }
    
    public List<Transaccion> listarPorTipoYFecha(Transaccion.TipoTransaccion tipo, LocalDate fecha) {
        List<Transaccion> transacciones = new ArrayList<>();
        
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(23, 59, 59);
        
        String sql = "SELECT * FROM transacciones WHERE tipo = ? AND fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tipo.toString());
            pstmt.setTimestamp(2, Timestamp.valueOf(inicioDia));
            pstmt.setTimestamp(3, Timestamp.valueOf(finDia));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transacciones.add(mapResultSetToTransaccion(rs));
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al listar transacciones por tipo y fecha: " + e.getMessage());
        }
        
        return transacciones;
    }
    
    public BigDecimal calcularTotalPorTipoYFecha(Transaccion.TipoTransaccion tipo, LocalDate fecha) {
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(23, 59, 59);
        
        String sql = "SELECT SUM(monto) FROM transacciones WHERE tipo = ? AND fecha BETWEEN ? AND ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tipo.toString());
            pstmt.setTimestamp(2, Timestamp.valueOf(inicioDia));
            pstmt.setTimestamp(3, Timestamp.valueOf(finDia));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal resultado = rs.getBigDecimal(1);
                    return resultado != null ? resultado : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al calcular total por tipo y fecha: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    public BigDecimal calcularTotalPorTipoYRango(Transaccion.TipoTransaccion tipo, LocalDateTime inicio, LocalDateTime fin) {
        String sql = "SELECT SUM(monto) FROM transacciones WHERE tipo = ? AND fecha BETWEEN ? AND ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tipo.toString());
            pstmt.setTimestamp(2, Timestamp.valueOf(inicio));
            pstmt.setTimestamp(3, Timestamp.valueOf(fin));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal resultado = rs.getBigDecimal(1);
                    return resultado != null ? resultado : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al calcular total por tipo y rango: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }
    
    private Transaccion mapResultSetToTransaccion(ResultSet rs) throws SQLException {
        Transaccion transaccion = new Transaccion();
        transaccion.setId(rs.getLong("id"));
        transaccion.setTipo(Transaccion.TipoTransaccion.valueOf(rs.getString("tipo")));
        transaccion.setConcepto(rs.getString("concepto"));
        transaccion.setMonto(rs.getBigDecimal("monto"));
        transaccion.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        
        // Obtener usuario
        Long usuarioId = rs.getLong("usuario_id");
        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorId(usuarioId);
        
        if (usuarioOpt.isPresent()) {
            transaccion.setUsuario(usuarioOpt.get());
        } else {
            logger.warning("No se encontró usuario con ID " + usuarioId);
        }
        
        // Obtener reserva (si existe)
        Long reservaId = rs.getLong("reserva_id");
        if (!rs.wasNull()) {
            Optional<Reserva> reservaOpt = reservaDAO.buscarPorId(reservaId);
            reservaOpt.ifPresent(transaccion::setReserva);
        }
        
        return transaccion;
    }
}
