package com.dam.dao.jpa;

import com.dam.dao.iJugadorDao;
import com.dam.models.Jugador;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Implementación JPA del DAO para la entidad Jugador
 * @author [Daniel Redondo Castaño]
 */
public class JugadorDaoJpaImpl implements iJugadorDao {

    private final EntityManager em;

    public JugadorDaoJpaImpl (EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Jugador jugador) {
        em.persist(jugador);
    }

    @Override
    public Jugador getById(Long id) {
        return em.find(Jugador.class, id);
    }

    @Override
    public List<Jugador> findAll() {
        return em.createQuery("SELECT j FROM Jugador j", Jugador.class)
                .getResultList();
    }

    @Override
    public Jugador update(Jugador jugador) {
        return em.merge(jugador);
    }

    @Override
    public void delete(Jugador jugador) {
        em.remove(jugador);
    }
}
