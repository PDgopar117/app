/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.ReservaDAO;
import com.elencanto.app.model.Reserva;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ReservasController {
    private static final Logger logger = Logger.getLogger(ReservasController.class.getName());
    
    // Aquí deberías declarar todos los componentes con fx:id del archivo Reservas.fxml
    // Por ejemplo:
    @FXML
    private TableView<Reserva> reservasTableView;
    
    @FXML
    private Button nuevaReservaButton;
    
    // Otros controles...
    
    private ReservaDAO reservaDAO;
    
    @FXML
    public void initialize() {
        try {
            // Inicializar DAO
            reservaDAO = new ReservaDAO();
            
            // Configurar controles
            
            // Cargar datos
            cargarReservas();
            
        } catch (Exception e) {
            logger.severe("Error al inicializar controlador de reservas: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo inicializar el controlador de reservas");
        }
    }
    
    private void cargarReservas() {
        try {
            List<Reserva> reservas = reservaDAO.obtenerTodas();
            // Configurar tabla con los datos
        } catch (SQLException e) {
            logger.severe("Error al cargar reservas: " + e.getMessage());
            mostrarAlerta("Error", "No se pudieron cargar las reservas");
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