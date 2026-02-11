package com.dam.services;

import com.dam.dao.jpa.*;
import com.dam.models.*;
import jakarta.persistence.EntityManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para cargar datos iniciales desde archivos CSV
 * @author [Daniel Redondo Castaño]
 */
public class DataLoaderService {

    public static void cargarDatosIniciales(EntityManager em) {
        System.out.println("\n========== CARGANDO DATOS INICIALES ==========");

        try {
            em.getTransaction().begin();

            Map<String, Ciudad> ciudades = cargarCiudades(em);
            System.out.println("✓ Ciudades cargadas: " + ciudades.size());

            Map<String, Patrocinador> patrocinadores = cargarPatrocinadores(em);
            System.out.println("✓ Patrocinadores cargados: " + patrocinadores.size());

            Map<String, Equipo> equipos = cargarEquipos(em, ciudades);
            System.out.println("✓ Equipos cargados: " + equipos.size());

            int jugadoresCount = cargarJugadores(em, equipos);
            System.out.println("✓ Jugadores cargados: " + jugadoresCount);

            asignarPatrocinadores(equipos, patrocinadores);
            System.out.println("✓ Patrocinadores asignados a equipos");

            em.getTransaction().commit();
            System.out.println("========== DATOS CARGADOS EXITOSAMENTE ==========\n");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("✗ Error al cargar datos iniciales: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Map<String, Ciudad> cargarCiudades(EntityManager em) throws Exception {
        Map<String, Ciudad> ciudades = new HashMap<>();
        CiudadDaoJpaImpl ciudadDao = new CiudadDaoJpaImpl(em);

        try (InputStream is = DataLoaderService.class.getResourceAsStream("/data/ciudades.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line = br.readLine(); // Saltar cabecera
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String nombre = parts[0].trim();
                    String pais = parts[1].trim();

                    Ciudad ciudad = new Ciudad(nombre, pais);
                    ciudadDao.save(ciudad);
                    ciudades.put(nombre, ciudad);
                }
            }
        }
        return ciudades;
    }


    private static Map<String, Patrocinador> cargarPatrocinadores(EntityManager em) throws Exception {
        Map<String, Patrocinador> patrocinadores = new HashMap<>();
        PatrocinadorDaoImpl patrocinadorDao = new PatrocinadorDaoImpl(em);

        try (InputStream is = DataLoaderService.class.getResourceAsStream("/data/patrocinadores.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line = br.readLine(); // Saltar cabecera
            while ((line = br.readLine()) != null) {
                String nombre = line.trim();
                if (!nombre.isEmpty()) {
                    Patrocinador patrocinador = new Patrocinador(nombre);
                    patrocinadorDao.save(patrocinador);
                    patrocinadores.put(nombre, patrocinador);
                }
            }
        }
        return patrocinadores;
    }

    private static Map<String, Equipo> cargarEquipos(EntityManager em, Map<String, Ciudad> ciudades) throws Exception {
        Map<String, Equipo> equipos = new HashMap<>();
        EquipoDaoJpaImpl equipoDao = new EquipoDaoJpaImpl(em);

        try (InputStream is = DataLoaderService.class.getResourceAsStream("/data/equipos.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line = br.readLine(); // Saltar cabecera
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String nombre = parts[0].trim();
                    String abreviatura = parts[1].trim();
                    String nombreCiudad = parts[2].trim();

                    Ciudad ciudad = ciudades.get(nombreCiudad);
                    if (ciudad != null) {
                        Equipo equipo = new Equipo(nombre, abreviatura, ciudad);
                        equipoDao.save(equipo);
                        equipos.put(nombre, equipo);
                    }
                }
            }
        }
        return equipos;
    }

    private static int cargarJugadores(EntityManager em, Map<String, Equipo> equipos) throws Exception {
        JugadorDaoJpaImpl jugadorDao = new JugadorDaoJpaImpl(em);
        int count = 0;

        try (InputStream is = DataLoaderService.class.getResourceAsStream("/data/jugadores.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line = br.readLine(); // Saltar cabecera
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String nombre = parts[0].trim();
                    String posicionStr = parts[1].trim();
                    String nombreEquipo = parts[2].trim();

                    // Convertir string a enum Posicion
                    Posicion posicion = null;
                    try {
                        posicion = Posicion.valueOf(posicionStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Posición inválida: " + posicionStr + " para jugador " + nombre);
                        continue;
                    }

                    Equipo equipo = equipos.get(nombreEquipo);
                    if (equipo != null) {
                        Jugador jugador = new Jugador(nombre, posicion, equipo);
                        equipo.addJugador(jugador);
                        jugadorDao.save(jugador);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static void asignarPatrocinadores(Map<String, Equipo> equipos, Map<String, Patrocinador> patrocinadores) {
        int index = 0;
        Patrocinador[] patArray = patrocinadores.values().toArray(new Patrocinador[0]);

        for (Equipo equipo : equipos.values()) {
            // Asignar 2-3 patrocinadores por equipo
            int numPatrocinadores = 2 + (index % 2); // Alterna entre 2 y 3
            for (int i = 0; i < numPatrocinadores && i < patArray.length; i++) {
                equipo.addPatrocinador(patArray[(index + i) % patArray.length]);
            }
            index++;
        }
    }
}


