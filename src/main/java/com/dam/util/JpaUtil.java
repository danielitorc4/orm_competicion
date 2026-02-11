package com.dam.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


/**
 * Esta clase se encarga de proporcionar una EntityManagerFactory para usarse en el proyecto
 * @author [Daniel Redondo Castaño]
 */
public class JpaUtil {

    private static final EntityManagerFactory entityManagerFactory = buildEntityManagerFactory();
    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            return Persistence.createEntityManagerFactory("jpamysql");
        } catch (Exception e) {
            System.err.println("Error al crear EnitityManagerFactory: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static void shutdown() {
        if (entityManagerFactory!=null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}