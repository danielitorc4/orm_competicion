package com.dam.dao;

import com.dam.models.Patrocinador;

import java.util.List;

public interface iPatrocinadorDao extends iGenericDao<Patrocinador> {

    @Override
    void save(Patrocinador patrocinador);
    @Override
    Patrocinador getById(Long id);
    @Override
    List<Patrocinador> findAll();
    @Override
    Patrocinador update(Patrocinador patrocinador);
    @Override
    void delete(Patrocinador patrocinador);

}
