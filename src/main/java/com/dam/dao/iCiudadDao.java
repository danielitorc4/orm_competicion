package com.dam.dao;

import com.dam.models.Ciudad;

import java.util.List;

/**
 * Interfaz DAO para la entidad Ciudad
 * @author [Daniel Redondo Castaño]
 */
public interface iCiudadDao extends iGenericDao<Ciudad> {

    @Override
    void save(Ciudad ciudad);
    @Override
    Ciudad getById(Long id);
    @Override
    List<Ciudad> findAll();
    @Override
    Ciudad update(Ciudad ciudad);
    @Override
    void delete(Ciudad ciudad);
}
