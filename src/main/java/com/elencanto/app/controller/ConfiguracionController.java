/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.model.*;
import com.elencanto.app.dao.*;
import com.elencanto.app.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConfiguracionController {
    private static final Logger logger = Logger.getLogger(ConfiguracionController.class.getName());
    
    @FXML
    private TabPane configTabPane;
    
    // Componentes para gestión de usuarios
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private ComboBox<String> rolComboBox;
    
    @FXML
    private TableView<Usuario> usuariosTable;
    
    @FXML
    private TableColumn<Usuario, String> usernameColumn;
    
    @FXML
    private TableColumn<Usuario, String> emailColumn;
    
    @FXML
    private TableColumn<Usuario, String> rolColumn;
    
    // Componentes para gestión de habitaciones
    @FXML
    private TextField numeroHabitacionField;
    
    @FXML
    private ComboBox<String> tipoHabitacionComboBox;
    
    @FXML
    private TextField tarifaField;
    
    @FXML
    private TextArea caracteristicasArea;
    
    @FXML
    private TableView<Habitacion> habitacionesTable;
    
    @FXML
    private TableColumn<Habitacion, String> numeroColumn;
    
    @FXML
    private TableColumn<Habitacion, String> tipoColumn;
    
    @FXML
    private TableColumn<Habitacion, Double> tarifaColumn;
    
    @FXML
    private TableColumn<Habitacion, String> estadoColumn;

    private UsuarioDAO usuarioDAO;
    private HabitacionDAO habitacionDAO;
    
    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();
        habitacionDAO = new HabitacionDAO();
        
        // Verificar si el usuario actual es administrador
        Usuario usuarioActual = SessionManager.getInstance().getUsuarioActual();
        if (usuarioActual == null || !Rol.ADMIN.equals(usuarioActual.getRol())) {
            configTabPane.getTabs().removeIf(tab -> 
                !tab.getText().equals("Cambiar Contraseña"));
            return;
        }
        
        // Inicializar ComboBoxes con valores de enums
        rolComboBox.setItems(FXCollections.observableArrayList(
            Arrays.stream(Rol.values())
                  .map(Rol::name)
                  .collect(Collectors.toList())
        ));
        
        tipoHabitacionComboBox.setItems(FXCollections.observableArrayList(
            Arrays.stream(TipoHabitacion.values())
                  .map(TipoHabitacion::name)
                  .collect(Collectors.toList())
        ));
        
        // Configurar columnas de la tabla de usuarios
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));
        
        // Configurar columnas de la tabla de habitaciones
        numeroColumn.setCellValueFactory(new PropertyValueFactory<>("numero"));
        tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        tarifaColumn.setCellValueFactory(new PropertyValueFactory<>("tarifa"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        // Cargar datos iniciales
        cargarUsuarios();
        cargarHabitaciones();
    }
    
    @FXML
    private void crearUsuario() {
        try {
            if (camposUsuarioValidos()) {
                Usuario usuario = new Usuario();
                usuario.setUsername(usernameField.getText());
                usuario.setPassword(PasswordHash.hash(passwordField.getText()));
                usuario.setEmail(emailField.getText());
                usuario.setRol(rolComboBox.getValue());
                
                if (usuarioDAO.crearUsuario(usuario)) {
                    mostrarAlerta("Éxito", "Usuario creado correctamente", Alert.AlertType.INFORMATION);
                    limpiarCamposUsuario();
                    cargarUsuarios();
                } else {
                    mostrarAlerta("Error", "No se pudo crear el usuario", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            logger.severe("Error al crear usuario: " + e.getMessage());
            mostrarAlerta("Error", "Error al crear usuario", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void crearHabitacion() {
        try {
            if (camposHabitacionValidos()) {
                Habitacion habitacion = new Habitacion();
                habitacion.setNumero(numeroHabitacionField.getText());
                habitacion.setTipo(TipoHabitacion.valueOf(tipoHabitacionComboBox.getValue()));
                habitacion.setTarifa(Double.parseDouble(tarifaField.getText()));
                habitacion.setCaracteristicas(caracteristicasArea.getText());
                habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
                
                if (habitacionDAO.crearHabitacion(habitacion)) {
                    mostrarAlerta("Éxito", "Habitación creada correctamente", Alert.AlertType.INFORMATION);
                    limpiarCamposHabitacion();
                    cargarHabitaciones();
                } else {
                    mostrarAlerta("Error", "No se pudo crear la habitación", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            logger.severe("Error al crear habitación: " + e.getMessage());
            mostrarAlerta("Error", "Error al crear habitación", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void crearBackup() {
        try {
            if (DatabaseUtil.crearBackup()) {
                mostrarAlerta("Éxito", "Backup creado correctamente", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se pudo crear el backup", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            logger.severe("Error al crear backup: " + e.getMessage());
            mostrarAlerta("Error", "Error al crear backup", Alert.AlertType.ERROR);
        }
    }
    
    private boolean camposUsuarioValidos() {
        if (usernameField.getText().isEmpty() || 
            passwordField.getText().isEmpty() || 
            emailField.getText().isEmpty() || 
            rolComboBox.getValue() == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    private boolean camposHabitacionValidos() {
        if (numeroHabitacionField.getText().isEmpty() || 
            tipoHabitacionComboBox.getValue() == null || 
            tarifaField.getText().isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
            return false;
        }
        try {
            Double.parseDouble(tarifaField.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La tarifa debe ser un número válido", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();
            usuariosTable.setItems(FXCollections.observableArrayList(usuarios));
        } catch (Exception e) {
            logger.severe("Error al cargar usuarios: " + e.getMessage());
        }
    }
    
    private void cargarHabitaciones() {
        try {
            List<Habitacion> habitaciones = habitacionDAO.obtenerTodas();
            habitacionesTable.setItems(FXCollections.observableArrayList(habitaciones));
        } catch (Exception e) {
            logger.severe("Error al cargar habitaciones: " + e.getMessage());
        }
    }
    
    private void limpiarCamposUsuario() {
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        rolComboBox.setValue(null);
    }
    
    private void limpiarCamposHabitacion() {
        numeroHabitacionField.clear();
        tipoHabitacionComboBox.setValue(null);
        tarifaField.clear();
        caracteristicasArea.clear();
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}