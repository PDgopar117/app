/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.util;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Gopar117
 */
public class ExportUtil {
    private static final Logger logger = Logger.getLogger(ExportUtil.class.getName());
    
    /**
     * Exporta datos a un archivo Excel
     * @param filePath Ruta del archivo
     * @param sheetName Nombre de la hoja
     * @param headers Encabezados de columnas
     * @param data Datos a exportar (lista de arrays de objetos)
     * @return true si la operación fue exitosa
     */
    public static boolean exportToExcel(String filePath, String sheetName, 
                                     String[] headers, List<Object[]> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            
            // Crear estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Crear fila de encabezados
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Crear filas de datos
            int rowNum = 1;
            for (Object[] rowData : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < rowData.length; i++) {
                    Cell cell = row.createCell(i);
                    
                    // Asignar valor según tipo
                    if (rowData[i] instanceof String) {
                        cell.setCellValue((String) rowData[i]);
                    } else if (rowData[i] instanceof Integer) {
                        cell.setCellValue((Integer) rowData[i]);
                    } else if (rowData[i] instanceof Double) {
                        cell.setCellValue((Double) rowData[i]);
                    } else if (rowData[i] instanceof LocalDate) {
                        cell.setCellValue(((LocalDate) rowData[i])
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    } else if (rowData[i] != null) {
                        cell.setCellValue(rowData[i].toString());
                    }
                }
            }
            
            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Escribir a archivo
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
            
            logger.info("Archivo Excel creado correctamente: " + filePath);
            return true;
        } catch (Exception e) {
            logger.severe("Error al exportar a Excel: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exporta datos a un archivo CSV
     * @param filePath Ruta del archivo
     * @param headers Encabezados de columnas
     * @param data Datos a exportar (lista de arrays de objetos)
     * @return true si la operación fue exitosa
     */
    public static boolean exportToCSV(String filePath, String[] headers, List<Object[]> data) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(fos, "UTF-8")) {
            
            // Escribir encabezados
            osw.write(String.join(",", headers) + "\n");
            
            // Escribir datos
            for (Object[] rowData : data) {
                StringBuilder sb = new StringBuilder();
                
                for (int i = 0; i < rowData.length; i++) {
                    String value = "";
                    
                    if (rowData[i] != null) {
                        if (rowData[i] instanceof String) {
                            // Escapar comillas y encerrar en comillas
                            value = "\"" + ((String) rowData[i]).replace("\"", "\"\"") + "\"";
                        } else if (rowData[i] instanceof LocalDate) {
                            value = ((LocalDate) rowData[i])
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        } else {
                            value = rowData[i].toString();
                        }
                    }
                    
                    sb.append(value);
                    
                    if (i < rowData.length - 1) {
                        sb.append(",");
                    }
                }
                
                osw.write(sb.toString() + "\n");
            }
            
            logger.info("Archivo CSV creado correctamente: " + filePath);
            return true;
        } catch (Exception e) {
            logger.severe("Error al exportar a CSV: " + e.getMessage());
            return false;
        }
    }
}
    
