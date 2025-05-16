/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.io.IOException;
import java.util.logging.Logger;

public class DashboardController {
    private static final Logger logger = Logger.getLogger(DashboardController.class.getName());

    @FXML
    private StackPane contentArea;

    @FXML
    private Button dashboardButton;
    
    @FXML
    private Button habitacionesButton;
    
    @FXML
    private Button reservasButton;
    
    @FXML
    private Button finanzasButton;
    
    @FXML
    private Button reportesButton;
    
    @FXML
    private Button configuracionButton;

    @FXML
    public void initialize() {
        mostrarContenidoInicial();
    }

    private void mostrarContenidoInicial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashboardInicio.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            actualizarMenuActivo("dashboardButton");
        } catch (IOException e) {
            logger.severe("Error al cargar la vista inicial: " + e.getMessage());
            mostrarContenidoPorDefecto();
        }
    }

    private void mostrarContenidoPorDefecto() {
        VBox contenidoDefault = new VBox(20);
        contenidoDefault.setAlignment(Pos.CENTER);
        contenidoDefault.setPadding(new Insets(20));
        
        Label bienvenida = new Label("Bienvenido al Sistema de Control");
        bienvenida.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label subtitulo = new Label("Seleccione una opción del menú para comenzar");
        subtitulo.setStyle("-fx-font-size: 16px;");
        
        contenidoDefault.getChildren().addAll(bienvenida, subtitulo);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(contenidoDefault);
    }

    @FXML
    private void handleDashboardClick() {
        mostrarContenidoInicial();
        actualizarMenuActivo("dashboardButton");
    }

    @FXML
    private void handleHabitacionesClick() {
        cargarVista("/fxml/Habitaciones.fxml", "habitacionesButton");
    }

    @FXML
    private void handleReservasClick() {
        cargarVista("/fxml/Reservas.fxml", "reservasButton");
    }

    @FXML
    private void handleFinanzasClick() {
        cargarVista("/fxml/Finanzas.fxml", "finanzasButton");
    }

    @FXML
    private void handleReportesClick() {
        cargarVista("/fxml/Reportes.fxml", "reportesButton");
    }

    @FXML
    private void handleConfiguracionClick() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Configuracion.fxml"));
            Parent configuracionView = loader.load();
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(configuracionView);
            
            actualizarMenuActivo("configuracionButton");
        } catch (IOException e) {
            logger.severe("Error al cargar la vista de configuración: " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cargar la configuración");
        }
    }

    @FXML
    private void handleCerrarSesion() {
        // Implementar lógica de cierre de sesión
    }

    private void cargarVista(String fxml, String botonId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            actualizarMenuActivo(botonId);
        } catch (IOException e) {
            logger.severe("Error al cargar la vista " + fxml + ": " + e.getMessage());
            mostrarAlerta("Error", "No se pudo cargar la vista");
        }
    }

    private void actualizarMenuActivo(String botonId) {
        // Remover la clase activa de todos los botones
        dashboardButton.getStyleClass().remove("menu-item-activo");
        habitacionesButton.getStyleClass().remove("menu-item-activo");
        reservasButton.getStyleClass().remove("menu-item-activo");
        finanzasButton.getStyleClass().remove("menu-item-activo");
        reportesButton.getStyleClass().remove("menu-item-activo");
        configuracionButton.getStyleClass().remove("menu-item-activo");
        
        // Agregar la clase activa al botón seleccionado
        switch (botonId) {
            case "dashboardButton":
                dashboardButton.getStyleClass().add("menu-item-activo");
                break;
            case "habitacionesButton":
                habitacionesButton.getStyleClass().add("menu-item-activo");
                break;
            case "reservasButton":
                reservasButton.getStyleClass().add("menu-item-activo");
                break;
            case "finanzasButton":
                finanzasButton.getStyleClass().add("menu-item-activo");
                break;
            case "reportesButton":
                reportesButton.getStyleClass().add("menu-item-activo");
                break;
            case "configuracionButton":
                configuracionButton.getStyleClass().add("menu-item-activo");
                break;
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