/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;
import com.elencanto.app.dao.ReservaDAO;
import com.elencanto.app.dao.TransaccionDAO;
import com.elencanto.app.model.Transaccion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;
/**
 *
 * @author Gopar117
 */
public class ReportesController {
    private static final Logger logger = Logger.getLogger(ReportesController.class.getName());
    
    @FXML
    private Button reporteVentasButton;
    
    @FXML
    private Button reporteOcupacionButton;
    
    @FXML
    private Button reporteFinancieroButton;
    
    private TransaccionDAO transaccionDAO = new TransaccionDAO();
    private ReservaDAO reservaDAO = new ReservaDAO();
    
    @FXML
    public void initialize() {
        configurarBotones();
    }
    
    private void configurarBotones() {
        reporteVentasButton.setOnAction(event -> abrirReporteVentas());
        reporteOcupacionButton.setOnAction(event -> abrirReporteOcupacion());
        reporteFinancieroButton.setOnAction(event -> abrirReporteFinanciero());
    }
    
    private void abrirReporteVentas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReporteVentas.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Reporte de Ventas");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe("Error al abrir reporte de ventas: " + e.getMessage());
        }
    }
    
    private void abrirReporteOcupacion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReporteOcupacion.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Reporte de Ocupación");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe("Error al abrir reporte de ocupación: " + e.getMessage());
        }
    }
    
    private void abrirReporteFinanciero() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReporteFinanciero.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Reporte Financiero");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe("Error al abrir reporte financiero: " + e.getMessage());
        }
    }
    
}
