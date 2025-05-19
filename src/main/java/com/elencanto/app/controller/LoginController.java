/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.UsuarioDAO;
import com.elencanto.app.model.Usuario;
import com.elencanto.app.util.PasswordHash;
import com.elencanto.app.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class LoginController {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    @FXML
    private TextField usuarioField;

    @FXML
    private PasswordField passwordField;

    private UsuarioDAO usuarioDAO;

    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();
    }

    @FXML
    private void handleLoginAction() {
        String username = usuarioField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese usuario y contraseña");
            return;
        }

        try {
            Usuario usuario = usuarioDAO.buscarPorUsername(username);
            if (usuario != null && PasswordHash.verify(password, usuario.getPassword())) {
                SessionManager.getInstance().setUsuarioActual(usuario);
                cargarDashboard();
            } else {
                mostrarAlerta("Error", "Usuario o contraseña incorrectos");
            }
        } catch (SQLException e) {
            logger.severe("Error al intentar iniciar sesión: " + e.getMessage());
            mostrarAlerta("Error", "Error al intentar iniciar sesión");
        }
    }
     @FXML
    private void handleRecuperarPassword() {  // Agregar este método faltante
        // Por ahora, solo mostrar un mensaje
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recuperar Contraseña");
        alert.setHeaderText(null);
        alert.setContentText("La funcionalidad de recuperación de contraseña no está implementada aún.");
        alert.showAndWait();
    }

    private void cargarDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) usuarioField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard - El Encanto");
            stage.show();
        } catch (IOException e) {
            logger.severe("Error al cargar el dashboard: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cargar el dashboard");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
