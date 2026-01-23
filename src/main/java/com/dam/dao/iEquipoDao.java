package com.dam.dao;

import com.dam.models.Equipo;

import java.util.List;

/**
 * Interfaz DAO para la entidad Equipo
 * @author [Daniel Redondo Castaño]
 */
public interface iEquipoDao extends iGenericDao<Equipo> {
    @Override
    void save(Equipo equipo);
    @Override
    Equipo getById(Long id);
    @Override
    List<Equipo> findAll();
    @Override
    Equipo update(Equipo equipo);
    @Override
    void delete(Equipo equipo);
}
