/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.model;


import java.math.BigDecimal;
/**
 *
 * @author Gopar117
 */
public class Habitacion {
     private Long id;
    private String numero;
    private TipoHabitacion tipo;
    private EstadoHabitacion estado;
    private BigDecimal tarifa;
    private String caracteristicas;
    
    public enum TipoHabitacion {
        STANDARD, SUITE
    }
    
    public enum EstadoHabitacion {
        DISPONIBLE, OCUPADA, LIMPIEZA
    }
    
    // Constructor
    public Habitacion(Long id, String numero, TipoHabitacion tipo, EstadoHabitacion estado, 
                      BigDecimal tarifa, String caracteristicas) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.estado = estado;
        this.tarifa = tarifa;
        this.caracteristicas = caracteristicas;
    }
    
    // Constructor vacío
    public Habitacion() {}
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoHabitacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoHabitacion tipo) {
        this.tipo = tipo;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public void setTarifa(BigDecimal tarifa) {
        this.tarifa = tarifa;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }
    
    @Override
    public String toString() {
        return "Habitacion{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", tipo=" + tipo +
                ", estado=" + estado +
                ", tarifa=" + tarifa +
                '}';
    }
}
