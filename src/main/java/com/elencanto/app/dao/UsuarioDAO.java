/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.dao;

import com.elencanto.app.model.Usuario;
import com.elencanto.app.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
/**
 *
 * @author Gopar117
 */
public class UsuarioDAO {
    private static final Logger logger = Logger.getLogger(UsuarioDAO.class.getName());
    private Connection connection;
    
    public UsuarioDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public boolean agregarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (username, password, email, rol) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getPassword());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getRol().toString());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getLong(1));
                }
            }
            
            return true;
        } catch (SQLException e) {
            logger.severe("Error al agregar usuario: " + e.getMessage());
            return false;
        }
    }
    
    public Optional<Usuario> buscarPorId(Long id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getLong("id"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setRol(Usuario.Rol.valueOf(rs.getString("rol")));
                    
                    return Optional.of(usuario);
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al buscar usuario por ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getLong("id"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setRol(Usuario.Rol.valueOf(rs.getString("rol")));
                    
                    return Optional.of(usuario);
                }
            }
        } catch (SQLException e) {
            logger.severe("Error al buscar usuario: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getLong("id"));
                usuario.setUsername(rs.getString("username"));
                usuario.setPassword(rs.getString("password"));
                usuario.setEmail(rs.getString("email"));
                usuario.setRol(Usuario.Rol.valueOf(rs.getString("rol")));
                
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            logger.severe("Error al listar usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET username = ?, password = ?, email = ?, rol = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getPassword());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getRol().toString());
            pstmt.setLong(5, usuario.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.severe("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarUsuario(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.severe("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
}
