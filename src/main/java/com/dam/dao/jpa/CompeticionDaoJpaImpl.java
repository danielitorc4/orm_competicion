package com.dam.dao.jpa;

import com.dam.dao.iCompeticionDao;
import com.dam.models.Competicion;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Implementación JPA del DAO para la entidad Competicion
 * @author [Daniel Redondo Castaño]
 */
public class CompeticionDaoJpaImpl implements iCompeticionDao {

    private final EntityManager em;

    public CompeticionDaoJpaImpl (EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Competicion competicion) {
        em.persist(competicion);
    }

    @Override
    public Competicion getById(Long id) {
        return em.find(Competicion.class, id);
    }

    @Override
    public List<Competicion> findAll() {
        return em.createQuery("SELECT c FROM Competicion c", Competicion.class)
                .getResultList();
    }

    @Override
    public Competicion update(Competicion competicion) {
        return em.merge(competicion);
    }

    @Override
    public void delete(Competicion competicion) {
        em.remove(competicion);
    }
}
