/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.util;

import com.elencanto.app.model.Usuario;
/**
 *
 * @author Gopar117
 */
public class SessionManager {
    private static SessionManager instance;
    private Usuario usuarioActual;
    
    private SessionManager() {
        // Constructor privado para Singleton
    }
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }
    
    public boolean existeSesion() {
        return usuarioActual != null;
    }
    
    public void cerrarSesion() {
        this.usuarioActual = null;
    }
    
    public boolean tienePermisoAdmin() {
        return usuarioActual != null && usuarioActual.getRol() == Usuario.Rol.ADMIN;
    }
}
