package com.dam.dao;

import com.dam.models.Fichaje;

import java.util.List;

/**
 * Interface DAO para la entidad Fichaje
 * @author [Daniel Redondo Castaño]
 */
public interface iFichajeDao extends iGenericDao<Fichaje> {

    @Override
    void save(Fichaje fichaje);
    @Override
    Fichaje getById(Long id);
    @Override
    List<Fichaje> findAll();
    @Override
    Fichaje update(Fichaje fichaje);
    @Override
    void delete(Fichaje fichaje);
}


