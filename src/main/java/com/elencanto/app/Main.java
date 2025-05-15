/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;  // Importación que falta
import javafx.scene.layout.StackPane;  // Importación que falta
import javafx.scene.control.Label;  
import java.net.URL;
import javafx.scene.control.*;  // Para Button, Label, TextField, etc.

// Layouts
import javafx.scene.layout.*;  // Para VBox, HBox, GridPane, etc.

// Eventos
import javafx.event.*;  // Para eventos como ActionEvent

// Geometría
import javafx.geometry.*;  // Para Pos, Insets, etc.
/**
 *
 * @author Gopar117
 */
public class Main extends Application {
 @Override
public void start(Stage primaryStage) throws Exception {
    try {
        System.out.println("Intentando cargar archivo FXML...");
        
        URL url = getClass().getClassLoader().getResource("fxml/Login.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        
        // Cargar el archivo CSS
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/styles.css").toExternalForm());
        
        primaryStage.setTitle("El Encanto - Sistema de Control");
        primaryStage.setScene(scene);
        primaryStage.show();
    } catch (Exception e) {
        System.err.println("Error al iniciar la aplicación:");
        e.printStackTrace();
        throw e;
    }
}
}