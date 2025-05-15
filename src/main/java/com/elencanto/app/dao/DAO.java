/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elencanto.app.dao;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author Gopar117
 */
public interface DAO<T, ID> {
    boolean agregar(T entidad);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
    boolean actualizar(T entidad);
    boolean eliminar(ID id);
}