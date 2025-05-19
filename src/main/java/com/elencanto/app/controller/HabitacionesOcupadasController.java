/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.HabitacionDAO;
import com.elencanto.app.model.Habitacion;
import com.elencanto.app.model.TipoHabitacion;
import com.elencanto.app.model.EstadoHabitacion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import java.util.List;
import java.sql.SQLException;
import java.util.logging.Logger;

public class HabitacionesOcupadasController {
    private static final Logger logger = Logger.getLogger(HabitacionesOcupadasController.class.getName());

    @FXML
    private TableView<Habitacion> habitacionesTable;

    private HabitacionDAO habitacionDAO;

    @FXML
    public void initialize() {
        habitacionDAO = new HabitacionDAO();
        cargarHabitaciones();
    }

    private void cargarHabitaciones() {
        try {
            List<Habitacion> habitaciones = habitacionDAO.obtenerTodas();
            habitacionesTable.setItems(FXCollections.observableArrayList(habitaciones));
        } catch (SQLException e) {
            logger.severe("Error al cargar habitaciones: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar las habitaciones");
        }
    }

    @FXML
    private void liberarHabitacion() {
        Habitacion habitacion = habitacionesTable.getSelectionModel().getSelectedItem();
        if (habitacion != null) {
            try {
                habitacion.setEstado(EstadoHabitacion.LIMPIEZA);
                if (habitacionDAO.actualizarHabitacion(habitacion)) {
                    cargarHabitaciones();
                    mostrarAlerta("Éxito", "Habitación marcada para limpieza");
                }
            } catch (SQLException e) {
                logger.severe("Error al liberar habitación: " + e.getMessage());
                mostrarAlerta("Error", "No se pudo liberar la habitación");
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
