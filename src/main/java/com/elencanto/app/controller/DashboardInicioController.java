/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import java.util.logging.Logger;

public class DashboardInicioController {
    private static final Logger logger = Logger.getLogger(DashboardInicioController.class.getName());

    @FXML
    private Label ocupacionLabel;
    
    @FXML
    private Label habitacionesLabel;
    
    @FXML
    private Label ingresosLabel;
    
    @FXML
    private Label egresosLabel;
    
    @FXML
    private GridPane habitacionesGrid;

    @FXML
    public void initialize() {
        cargarEstadisticas();
        cargarHabitaciones();
    }

    @FXML
    private void handleNuevaReserva() {
        // Implementar lógica para nueva reserva
        logger.info("Abriendo formulario de nueva reserva...");
    }

    private void cargarEstadisticas() {
        // Aquí cargarías los datos reales desde tu base de datos
        ocupacionLabel.setText("75%");
        habitacionesLabel.setText("8");
        ingresosLabel.setText("$458.900");
        egresosLabel.setText("$156.700");
    }

    private void cargarHabitaciones() {
        // Ejemplo de cómo agregar una habitación
        for (int i = 0; i < 5; i++) {
            VBox habitacionCard = crearTarjetaHabitacion(i + 1);
            habitacionesGrid.add(habitacionCard, i % 4, i / 4);
        }
    }

    private VBox crearTarjetaHabitacion(int numero) {
        VBox card = new VBox();
        card.getStyleClass().add("room-card");
        
        HBox header = new HBox();
        Label numeroLabel = new Label("Hab. " + String.format("%02d", numero));
        numeroLabel.getStyleClass().add("room-number");
        
        Circle statusCircle = new Circle(4);
        statusCircle.getStyleClass().addAll("status-circle", "occupied");
        
        header.getChildren().addAll(numeroLabel, statusCircle);
        
        Label tipoLabel = new Label("standard");
        tipoLabel.getStyleClass().add("room-type");
        
        Label horaLabel = new Label("14:30");
        horaLabel.getStyleClass().add("room-time");
        
        card.getChildren().addAll(header, tipoLabel, horaLabel);
        return card;
    }
}