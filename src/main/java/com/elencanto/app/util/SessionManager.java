/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.util;

import com.elencanto.app.model.Usuario;
import com.elencanto.app.model.Rol;

public class SessionManager {
    private static SessionManager instance;
    private Usuario usuarioActual;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }
    
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public void cerrarSesion() {
        usuarioActual = null;
    }
    
    public boolean isAdmin() {
        return usuarioActual != null && Rol.ADMIN.equals(usuarioActual.getRol());
    }
}
