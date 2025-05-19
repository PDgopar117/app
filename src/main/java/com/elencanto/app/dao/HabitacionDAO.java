/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.dao;

import com.elencanto.app.model.Habitacion;
import com.elencanto.app.model.TipoHabitacion;
import com.elencanto.app.model.EstadoHabitacion;
import com.elencanto.app.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class HabitacionDAO {
    private static final Logger logger = Logger.getLogger(HabitacionDAO.class.getName());

    public boolean crearHabitacion(Habitacion habitacion) throws SQLException {
        String sql = "INSERT INTO habitaciones (numero, tipo, estado, tarifa, caracteristicas) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, habitacion.getNumero());
            stmt.setString(2, habitacion.getTipo().name());
            stmt.setString(3, habitacion.getEstado().name());
            stmt.setDouble(4, habitacion.getTarifa());
            stmt.setString(5, habitacion.getCaracteristicas());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Habitacion> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM habitaciones WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Habitacion habitacion = new Habitacion();
                    habitacion.setId(rs.getLong("id"));
                    habitacion.setNumero(rs.getString("numero"));
                    habitacion.setTipo(TipoHabitacion.valueOf(rs.getString("tipo")));
                    habitacion.setEstado(EstadoHabitacion.valueOf(rs.getString("estado")));
                    habitacion.setTarifa(rs.getDouble("tarifa"));
                    habitacion.setCaracteristicas(rs.getString("caracteristicas"));
                    return Optional.of(habitacion);
                }
            }
        }
        return Optional.empty();
    }

    public List<Habitacion> obtenerTodas() throws SQLException {
        List<Habitacion> habitaciones = new ArrayList<>();
        String sql = "SELECT * FROM habitaciones";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Habitacion habitacion = new Habitacion();
                habitacion.setId(rs.getLong("id"));
                habitacion.setNumero(rs.getString("numero"));
                habitacion.setTipo(TipoHabitacion.valueOf(rs.getString("tipo")));
                habitacion.setEstado(EstadoHabitacion.valueOf(rs.getString("estado")));
                habitacion.setTarifa(rs.getDouble("tarifa"));
                habitacion.setCaracteristicas(rs.getString("caracteristicas"));
                habitaciones.add(habitacion);
            }
        }
        return habitaciones;
    }

    public boolean actualizarHabitacion(Habitacion habitacion) throws SQLException {
        String sql = "UPDATE habitaciones SET numero = ?, tipo = ?, estado = ?, tarifa = ?, caracteristicas = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, habitacion.getNumero());
            stmt.setString(2, habitacion.getTipo().name());
            stmt.setString(3, habitacion.getEstado().name());
            stmt.setDouble(4, habitacion.getTarifa());
            stmt.setString(5, habitacion.getCaracteristicas());
            stmt.setLong(6, habitacion.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
}