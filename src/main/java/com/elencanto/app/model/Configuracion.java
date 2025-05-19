/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.model;

public class Configuracion {
    private Long id;
    private String nombreNegocio;
    private String direccion;
    private String telefono;
    
    public Configuracion() {}
    
    public Configuracion(Long id, String nombreNegocio, String direccion, String telefono) {
        this.id = id;
        this.nombreNegocio = nombreNegocio;
        this.direccion = direccion;
        this.telefono = telefono;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombreNegocio() {
        return nombreNegocio;
    }
    
    public void setNombreNegocio(String nombreNegocio) {
        this.nombreNegocio = nombreNegocio;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}