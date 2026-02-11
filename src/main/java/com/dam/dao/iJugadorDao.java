package com.dam.dao;

import com.dam.models.Jugador;

import java.util.List;

public interface iJugadorDao extends iGenericDao<Jugador> {

    @Override
    void save(Jugador jugador);
    @Override
    Jugador getById(Long id);
    @Override
    List<Jugador> findAll();
    @Override
    Jugador update(Jugador jugador);
    @Override
    void delete(Jugador jugador);
}
