package com.dam.dao.jpa;

import com.dam.dao.iPatrocinadorDao;
import com.dam.models.Patrocinador;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Implementación JPA del DAO para la entidad Patrocinador
 * @author [Daniel Redondo Castaño]
 */
public class PatrocinadorDaoImpl implements iPatrocinadorDao {

    private final EntityManager em;

    public PatrocinadorDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Patrocinador patrocinador) {
        em.persist(patrocinador);
    }

    @Override
    public Patrocinador getById(Long id) {
        return em.find(Patrocinador.class, id);
    }

    @Override
    public List<Patrocinador> findAll() {
        return em.createQuery("SELECT p FROM Patrocinador p", Patrocinador.class)
                .getResultList();
    }

    @Override
    public Patrocinador update(Patrocinador patrocinador) {
        return em.merge(patrocinador);
    }

    @Override
    public void delete(Patrocinador patrocinador) {
        em.remove(patrocinador);
    }
}
