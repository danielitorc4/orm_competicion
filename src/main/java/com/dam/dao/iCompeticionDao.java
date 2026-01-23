package com.dam.dao;

import com.dam.models.Competicion;

import java.util.List;

/**
 * Interfaz DAO para la entidad Competicion
 * @author [Daniel Redondo Castaño]
 */
public interface iCompeticionDao extends iGenericDao<Competicion> {

    @Override
    void save(Competicion competicion);
    @Override
    Competicion getById(Long id);
    @Override
    List<Competicion> findAll();
    @Override
    Competicion update(Competicion competicion);
    @Override
    void delete(Competicion competicion);

}
