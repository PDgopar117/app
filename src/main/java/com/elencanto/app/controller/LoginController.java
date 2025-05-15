/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.UsuarioDAO;
import com.elencanto.app.model.Usuario;
import com.elencanto.app.util.PasswordHash;
import com.elencanto.app.util.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;
/**
 *
 * @author Gopar117
 */
public class LoginController {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    
    @FXML
    private TextField usuarioField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Hyperlink recuperarPasswordButton;
    
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    @FXML
    public void initialize() {
        // Configuración inicial del controlador
    }
    
    @FXML
    private void handleLoginAction(ActionEvent event) {
        String username = usuarioField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error de validación", "Todos los campos son obligatorios.");
            return;
        }
        
        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorUsername(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // En un sistema real, verificar hash de contraseña
            if (verificarPassword(password, usuario.getPassword())) {
                // Iniciar sesión
                SessionManager.getInstance().setUsuarioActual(usuario);
                
                try {
                    // Cargar el dashboard
                     FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("/fxml/Dashboard.fxml"));
    Parent root = loader.load();
    
    Scene scene = new Scene(root);
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    
    stage.setTitle("El Encanto - Panel Principal");
    stage.setScene(scene);
    stage.setResizable(true);
    stage.setMaximized(true);
    stage.show();
    
    logger.info("Usuario " + username + " inició sesión correctamente");
} catch (IOException e) {
    logger.severe("Error al cargar el dashboard: " + e.getMessage() + "\nCausa: " + e.getCause());
    e.printStackTrace(); // Imprime el stacktrace completo para depuración
    mostrarAlerta("Error", "No se pudo cargar la siguiente pantalla: " + e.getMessage());
}
            } else {
                mostrarAlerta("Error de autenticación", "Contraseña incorrecta.");
            }
        } else {
            mostrarAlerta("Error de autenticación", "El usuario no existe.");
        }
    }
    
    @FXML
    private void handleRecuperarPassword(ActionEvent event) {
        mostrarAlerta("Recuperar contraseña", "Se enviará un enlace de recuperación al correo registrado.");
        // Aquí iría la lógica para recuperar la contraseña
    }
    
    private boolean verificarPassword(String inputPassword, String storedPassword) {
        // En un sistema real, usar BCrypt o similar
        // Por ahora, comparación simple para demo
        return inputPassword.equals(storedPassword);
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
