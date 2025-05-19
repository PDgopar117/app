/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.HabitacionDAO;
import com.elencanto.app.model.Habitacion;
import com.elencanto.app.model.EstadoHabitacion;
import com.elencanto.app.model.TipoHabitacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class HabitacionesController {
    private static final Logger logger = Logger.getLogger(HabitacionesController.class.getName());

    @FXML
    private Button agregarHabitacionButton;

    @FXML
    private Label totalHabitacionesLabel;

    @FXML
    private Label habitacionesDisponiblesLabel;

    @FXML
    private Label habitacionesOcupadasLabel;

    @FXML
    private Label habitacionesLimpiezaLabel;

    @FXML
    private TextField buscarField;

    @FXML
    private ComboBox<String> filtroTipoComboBox;

    @FXML
    private ComboBox<String> filtroEstadoComboBox;

    @FXML
    private TableView<Habitacion> habitacionesTableView;

    @FXML
    private TableColumn<Habitacion, String> numeroColumn;

    @FXML
    private TableColumn<Habitacion, String> tipoColumn;

    @FXML
    private TableColumn<Habitacion, String> estadoColumn;

    @FXML
    private TableColumn<Habitacion, Double> tarifaColumn;

    @FXML
    private TableColumn<Habitacion, Void> accionesColumn;

    private HabitacionDAO habitacionDAO;

    @FXML
    public void initialize() {
        try {
            // Inicializar DAO
            habitacionDAO = new HabitacionDAO();
            
            // Configurar columnas
            numeroColumn.setCellValueFactory(new PropertyValueFactory<>("numero"));
            tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
            estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
            tarifaColumn.setCellValueFactory(new PropertyValueFactory<>("tarifa"));
            
            // Configurar filtros
            filtroTipoComboBox.getItems().add("Todos");
            for (TipoHabitacion tipo : TipoHabitacion.values()) {
                filtroTipoComboBox.getItems().add(tipo.name());
            }
            
            filtroEstadoComboBox.getItems().add("Todos");
            for (EstadoHabitacion estado : EstadoHabitacion.values()) {
                filtroEstadoComboBox.getItems().add(estado.name());
            }
            
            // Cargar datos
            cargarHabitaciones();
            
        } catch (Exception e) {
            logger.severe("Error al inicializar controlador: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo inicializar el controlador de habitaciones");
        }
    }

    private void cargarHabitaciones() {
        try {
            List<Habitacion> habitaciones = habitacionDAO.obtenerTodas();
            habitacionesTableView.setItems(FXCollections.observableArrayList(habitaciones));
            
            // Actualizar contadores
            actualizarContadores(habitaciones);
            
        } catch (SQLException e) {
            logger.severe("Error al cargar habitaciones: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar las habitaciones");
        }
    }

    private void actualizarContadores(List<Habitacion> habitaciones) {
        int total = habitaciones.size();
        int disponibles = 0;
        int ocupadas = 0;
        int limpieza = 0;
        
        for (Habitacion h : habitaciones) {
            switch (h.getEstado()) {
                case DISPONIBLE:
                    disponibles++;
                    break;
                case OCUPADA:
                    ocupadas++;
                    break;
                case LIMPIEZA:
                    limpieza++;
                    break;
            }
        }
        
        totalHabitacionesLabel.setText(String.valueOf(total));
        habitacionesDisponiblesLabel.setText(String.valueOf(disponibles));
        habitacionesOcupadasLabel.setText(String.valueOf(ocupadas));
        habitacionesLimpiezaLabel.setText(String.valueOf(limpieza));
    }

    @FXML
    private void agregarHabitacion() {
        // Implementar lógica para agregar habitación
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
