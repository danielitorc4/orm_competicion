package com.dam.dao;

import java.util.List;

/**
 * Interfaz genérica para operaciones CRUD
 * @param <T> Tipo de entidad
 * @author [Daniel Redondo Castaño]
 */
public interface iGenericDao<T> {
    void save(T entity);
    T getById(Long id);
    List<T> findAll();
    T update(T entity);
    void delete(T entity);

}
