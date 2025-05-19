/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.util;

import org.mindrot.jbcrypt.BCrypt;
import java.util.logging.Logger;

public class PasswordHash {
    private static final Logger logger = Logger.getLogger(PasswordHash.class.getName());

    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verify(String password, String hashedPassword) {
        try {
            // Si el hash no parece tener formato BCrypt, hacer una comparación directa
        if (hashedPassword == null || !hashedPassword.startsWith("$2")) {
            logger.warning("Hash no tiene formato BCrypt, realizando comparación directa para pruebas");
            return password.equals(hashedPassword);
        }
        
        return BCrypt.checkpw(password, hashedPassword);
    } catch (IllegalArgumentException e) {
        logger.severe("Error al verificar contraseña: " + e.getMessage());
        return false;
            
            //return BCrypt.checkpw(password, hashedPassword);
        } //catch (IllegalArgumentException e) {
            //logger.severe("Error al verificar contraseña: " + e.getMessage());
            //return false;
        //}
    }
}
