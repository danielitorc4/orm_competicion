package com.dam.dao.jpa;

import com.dam.dao.iEquipoDao;
import com.dam.models.Equipo;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Implementación JPA del DAO para la entidad Equipo
 * @author [Daniel Redondo Castaño]
 */
public class EquipoDaoJpaImpl implements iEquipoDao {

    private final EntityManager em;

    public EquipoDaoJpaImpl (EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Equipo equipo) {
        em.persist(equipo);
    }

    @Override
    public Equipo getById(Long id) {
        return em.find(Equipo.class, id);
    }

    @Override
    public List<Equipo> findAll() {
        return em.createQuery("SELECT e FROM Equipo e", Equipo.class)
                .getResultList();
    }

    @Override
    public Equipo update(Equipo equipo) {
        return em.merge(equipo);
    }

    @Override
    public void delete(Equipo equipo) {
        em.remove(equipo);
    }
}
