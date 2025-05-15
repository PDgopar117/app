/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.UsuarioDAO;
import com.elencanto.app.model.Usuario;
import com.elencanto.app.util.DatabaseUtil;
import com.elencanto.app.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ConfiguracionController {
    private static final Logger logger = Logger.getLogger(ConfiguracionController.class.getName());
    
    // Referencias a los contenedores principales
    @FXML
    private VBox contenidoConfiguracion;
    
    @FXML
    private VBox mensajeErrorPermisos;
    
    @FXML
    private Button volverDashboardButton;
    
    @FXML
    private TabPane tabPane;
    
    // Pestaña de usuarios
    @FXML
    private TableView<Usuario> usuariosTable;
    
    @FXML
    private TableColumn<Usuario, String> usernameColumn;
    
    @FXML
    private TableColumn<Usuario, String> emailColumn;
    
    @FXML
    private TableColumn<Usuario, Usuario.Rol> rolColumn;
    
    @FXML
    private Button agregarUsuarioButton;
    
    @FXML
    private Button editarUsuarioButton;
    
    @FXML
    private Button eliminarUsuarioButton;
    
    // Pestaña de backups
    @FXML
    private Button crearBackupButton;
    
    @FXML
    private Button restaurarBackupButton;
    
    @FXML
    private Label ultimoBackupLabel;
    
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    @FXML
    public void initialize() {
        // Verificar permisos de administrador
        if (!SessionManager.getInstance().tienePermisoAdmin()) {
            // Ocultar contenido de configuración
            contenidoConfiguracion.setVisible(false);
            
            // Mostrar mensaje de error
            mensajeErrorPermisos.setVisible(true);
            
            // Configurar botón para volver al dashboard
            volverDashboardButton.setOnAction(event -> volverAlDashboard());
            
            return;
        }
        
        // Si tiene permisos, mostrar contenido normal
        contenidoConfiguracion.setVisible(true);
        mensajeErrorPermisos.setVisible(false);
        
        configurarTablaUsuarios();
        configurarBotones();
        
        // Cargar datos iniciales
        cargarUsuarios();
    }
    
    private void volverAlDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) volverDashboardButton.getScene().getWindow();
            
            stage.setTitle("El Encanto - Panel Principal");
            stage.setScene(scene);
            
            logger.info("Regresando al dashboard desde configuración");
        } catch (IOException e) {
            logger.severe("Error al volver al dashboard: " + e.getMessage());
        }
    }
    
    // El resto del controlador queda igual que el código original que compartiste
    
    private void configurarTablaUsuarios() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));
    }
    
    private void configurarBotones() {
        agregarUsuarioButton.setOnAction(event -> mostrarDialogoUsuario(null));
        
        editarUsuarioButton.setOnAction(event -> {
            Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                mostrarDialogoUsuario(seleccionado);
            } else {
                mostrarAlerta("Selección requerida", "Por favor, selecciona un usuario para editar.");
            }
        });
        
        eliminarUsuarioButton.setOnAction(event -> {
            Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                confirmarEliminarUsuario(seleccionado);
            } else {
                mostrarAlerta("Selección requerida", "Por favor, selecciona un usuario para eliminar.");
            }
        });
        
        crearBackupButton.setOnAction(event -> crearBackup());
        restaurarBackupButton.setOnAction(event -> seleccionarBackupParaRestaurar());
    }
    
    private void cargarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        ObservableList<Usuario> data = FXCollections.observableArrayList(usuarios);
        usuariosTable.setItems(data);
    }
    
    private void mostrarDialogoUsuario(Usuario usuario) {
        // Código original sin cambios
    }
    
    private void confirmarEliminarUsuario(Usuario usuario) {
        // Código original sin cambios
    }
    
    private void crearBackup() {
        // Código original sin cambios
    }
    
    private void seleccionarBackupParaRestaurar() {
        // Código original sin cambios
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
