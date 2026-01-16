package com.dam.dao;

import com.dam.models.Equipo;

import java.util.List;

public interface iEquipoDao {
    void save(Equipo equipo);
    Equipo getById(Long id);
    List<Equipo> findAll();
    Equipo update(Equipo equipo);
    void delete(Equipo equipo);
}
