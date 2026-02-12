package com.dam.dao.jpa;

import com.dam.dao.iFichajeDao;
import com.dam.models.Fichaje;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Implementación JPA del DAO para la entidad Fichaje
 * @author [Daniel Redondo Castaño]
 */
public class FichajeDaoJpaImpl implements iFichajeDao {

    private final EntityManager em;

    public FichajeDaoJpaImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Fichaje fichaje) {
        em.persist(fichaje);
    }

    @Override
    public Fichaje getById(Long id) {
        return em.find(Fichaje.class, id);
    }

    @Override
    public List<Fichaje> findAll() {
        return em.createQuery("SELECT f FROM Fichaje f", Fichaje.class)
                .getResultList();
    }

    @Override
    public Fichaje update(Fichaje fichaje) {
        return em.merge(fichaje);
    }

    @Override
    public void delete(Fichaje fichaje) {
        em.remove(fichaje);
    }
}

