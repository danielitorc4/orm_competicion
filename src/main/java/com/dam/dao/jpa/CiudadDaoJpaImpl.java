package com.dam.dao.jpa;

import com.dam.dao.iCiudadDao;
import com.dam.models.Ciudad;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Implementación JPA del DAO para la entidad Ciudad
 * @author [Daniel Redondo Castaño]
 */
public class CiudadDaoJpaImpl implements iCiudadDao {

    private final EntityManager em;

    public CiudadDaoJpaImpl (EntityManager em) {
        this.em = em;
    }
    @Override
    public void save(Ciudad ciudad) {
        em.persist(ciudad);
    }

    @Override
    public Ciudad getById(Long id) {
        return em.find(Ciudad.class, id);
    }

    @Override
    public List<Ciudad> findAll() {
        return em.createQuery("SELECT c FROM Ciudad c", Ciudad.class)
                .getResultList();
    }

    @Override
    public Ciudad update(Ciudad ciudad) {
        return em.merge(ciudad);
    }

    @Override
    public void delete(Ciudad ciudad) {
        em.remove(ciudad);
    }
}
