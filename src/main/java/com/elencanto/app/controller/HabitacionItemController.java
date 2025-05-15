/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;
import com.elencanto.app.model.Habitacion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
/**
 *
 * @author Gopar117
 */
public class HabitacionItemController {
     @FXML
    private Label numeroLabel;
    
    @FXML
    private Label tipoLabel;
    
    @FXML
    private Circle estadoCircle;
    
    @FXML
    private Label horaLabel;
    
    public void configurarHabitacion(Habitacion habitacion) {
        numeroLabel.setText("Hab. " + habitacion.getNumero());
        tipoLabel.setText(habitacion.getTipo().toString().toLowerCase());
        
        // Configurar color según estado
        switch (habitacion.getEstado()) {
            case DISPONIBLE:
                estadoCircle.getStyleClass().add("estado-disponible");
                break;
            case OCUPADA:
                estadoCircle.getStyleClass().add("estado-ocupada");
                break;
            case LIMPIEZA:
                estadoCircle.getStyleClass().add("estado-limpieza");
                break;
        }
        
        // Mostrar hora actual como ejemplo (en un sistema real mostraría check-out estimado)
        horaLabel.setText("14:30");
    }
}
