package com.dam;

import com.dam.util.JpaUtil;
import jakarta.persistence.EntityManager;

/**
 * Hello world!
 *
 */
public class SimulacionMain
{
    public static void main( String[] args )
    {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        em.close();
        JpaUtil.shutdown();
    }
}
