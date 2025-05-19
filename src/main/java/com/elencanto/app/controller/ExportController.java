/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.controller;

import com.elencanto.app.dao.TransaccionDAO;
import com.elencanto.app.dao.ReservaDAO;
import com.elencanto.app.model.Transaccion;
import com.elencanto.app.model.Reserva;
import com.elencanto.app.util.ExportUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDate;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javafx.scene.control.Alert.AlertType;

public class ExportController {
    private static final Logger logger = Logger.getLogger(ExportController.class.getName());

    @FXML
    private DatePicker fechaInicioPicker;

    @FXML
    private DatePicker fechaFinPicker;

    @FXML
    private CheckBox exportarTransaccionesCheck;

    @FXML
    private CheckBox exportarReservasCheck;

    @FXML
    private Label statusLabel;

    @FXML
    private Button exportarButton;

    private TransaccionDAO transaccionDAO;
    private ReservaDAO reservaDAO;

    @FXML
    public void initialize() {
        transaccionDAO = new TransaccionDAO();
        reservaDAO = new ReservaDAO();

        // Configurar fechas iniciales (último mes)
        fechaInicioPicker.setValue(LocalDate.now().minusMonths(1));
        fechaFinPicker.setValue(LocalDate.now());

        // Configurar listeners para validación
        exportarTransaccionesCheck.selectedProperty().addListener((obs, old, newValue) -> 
            validarSeleccion());
        exportarReservasCheck.selectedProperty().addListener((obs, old, newValue) -> 
            validarSeleccion());

        validarSeleccion();
    }

    private void validarSeleccion() {
        boolean algunaSeleccion = exportarTransaccionesCheck.isSelected() || 
                                exportarReservasCheck.isSelected();
        exportarButton.setDisable(!algunaSeleccion);
    }

    @FXML
    private void exportar() {
        if (!validarFechas()) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte Excel");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        String nombreArchivo = "Reporte_" + 
                             fechaInicioPicker.getValue().toString() + "_a_" + 
                             fechaFinPicker.getValue().toString() + ".xlsx";
        fileChooser.setInitialFileName(nombreArchivo);

        File file = fileChooser.showSaveDialog(exportarButton.getScene().getWindow());
        if (file != null) {
            try {
                if (exportarTransaccionesCheck.isSelected()) {
                    exportarTransacciones(file.getAbsolutePath());
                }
                if (exportarReservasCheck.isSelected()) {
                    String rutaReservas = file.getAbsolutePath().replace(".xlsx", "_reservas.xlsx");
                    exportarReservas(rutaReservas);
                }

                mostrarAlerta(AlertType.INFORMATION, "Éxito", 
                    "Los reportes se han exportado correctamente");
                
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al exportar datos", e);
                mostrarAlerta(AlertType.ERROR, "Error", 
                    "No se pudieron exportar los datos: " + e.getMessage());
            }
        }
    }

    private void exportarTransacciones(String rutaArchivo) throws SQLException {
        List<Transaccion> transacciones = transaccionDAO.obtenerPorRangoFechas(
            fechaInicioPicker.getValue().atStartOfDay(),
            fechaFinPicker.getValue().atTime(23, 59, 59)
        );

        if (transacciones.isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Advertencia", 
                "No hay transacciones para exportar en el rango de fechas seleccionado");
            return;
        }

        if (!ExportUtil.exportarTransaccionesAExcel(transacciones, rutaArchivo)) {
            throw new SQLException("Error al exportar transacciones a Excel");
        }
    }

    private void exportarReservas(String rutaArchivo) throws SQLException {
        List<Reserva> reservas = reservaDAO.obtenerPorRangoFechas(
            fechaInicioPicker.getValue().atStartOfDay(),
            fechaFinPicker.getValue().atTime(23, 59, 59)
        );

        if (reservas.isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Advertencia", 
                "No hay reservas para exportar en el rango de fechas seleccionado");
            return;
        }

        if (!ExportUtil.exportarReservasAExcel(reservas, rutaArchivo)) {
            throw new SQLException("Error al exportar reservas a Excel");
        }
    }

    private boolean validarFechas() {
        if (fechaInicioPicker.getValue() == null || fechaFinPicker.getValue() == null) {
            mostrarAlerta(AlertType.ERROR, "Error", "Debe seleccionar ambas fechas");
            return false;
        }

        if (fechaInicioPicker.getValue().isAfter(fechaFinPicker.getValue())) {
            mostrarAlerta(AlertType.ERROR, "Error", 
                "La fecha de inicio debe ser anterior a la fecha fin");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void cancelar() {
        // Cerrar la ventana
        exportarButton.getScene().getWindow().hide();
    }
}
