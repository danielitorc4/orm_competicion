package com.dam;

import com.dam.models.Competicion;
import com.dam.services.DataLoaderService;
import com.dam.services.SimulacionService;
import com.dam.util.JpaUtil;
import jakarta.persistence.EntityManager;

/**
 * Esta clase se encarga de inicializar la aplicación, es el Main
 * @author [Daniel Redondo Castaño]
 */
public class SimulacionMain
{
    public static void main( String[] args )
    {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        // Cargar datos iniciales desde CSV
        DataLoaderService.cargarDatosIniciales(em);

        // Crear competición
        Competicion LEC = new Competicion("LEC", "League of Legends", "EUW", 10);
        System.out.println("Competición creada: " + LEC.getNombre() + " de " + LEC.getJuego());

        // Simular competición
        SimulacionService.simularCompeticion(LEC, em);

        em.close();
        JpaUtil.shutdown();
    }
}
