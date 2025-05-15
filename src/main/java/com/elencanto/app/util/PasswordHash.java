/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;
/**
 *
 * @author Gopar117
 */
public class PasswordHash {

    private static final Logger logger = Logger.getLogger(PasswordHash.class.getName());
    
    public static String generarHash(String password) {
        try {
            // Generar salt aleatorio
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Crear hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Combinar salt y hash
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            // Codificar a base64
            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            logger.severe("Error al generar hash: " + e.getMessage());
            return null;
        }
    }
    
    public static boolean verificarPassword(String password, String storedHash) {
        try {
            // Decodificar el hash almacenado
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            // Extraer salt (primeros 16 bytes)
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, salt.length);
            
            // Generar hash con la contraseña proporcionada
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Comparar los hashes
            byte[] storedHashBytes = new byte[combined.length - salt.length];
            System.arraycopy(combined, salt.length, storedHashBytes, 0, storedHashBytes.length);
            
            // Comparación byte a byte
            if (storedHashBytes.length != hashedPassword.length) {
                return false;
            }
            
            for (int i = 0; i < hashedPassword.length; i++) {
                if (hashedPassword[i] != storedHashBytes[i]) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            logger.severe("Error al verificar contraseña: " + e.getMessage());
            return false;
        }
    }
}
