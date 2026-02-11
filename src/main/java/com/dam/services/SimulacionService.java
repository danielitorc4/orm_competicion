package com.dam.services;

import com.dam.dao.jpa.EquipoDaoJpaImpl;
import com.dam.models.Competicion;
import com.dam.models.Equipo;
import com.dam.models.Jugador;
import com.dam.models.Posicion;
import jakarta.persistence.EntityManager;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/*
* Esta clase se encarga de simular el matchmaking entre equipo y las altas y bajas
* Simula la competición entera, validando que se cumplan los requisitos
* @author [Daniel Redondo Castaño]
 */
public class SimulacionService {

    public static void simularCompeticion(Competicion competicion, EntityManager em) {
        System.out.println("Simulando competición: " + competicion.getNombre());

        EquipoDaoJpaImpl equipoDaoJpa = new EquipoDaoJpaImpl(em);

        List<Equipo> equipos = equipoDaoJpa.findAll();

        if (equipos.isEmpty()) {
            System.out.println("No hay equipos registrados para la competición " + competicion.getNombre());
            return;
        }

        for (Equipo equipo : equipos) {
            System.out.println("\nEquipo participante: " + equipo.getNombre());
            System.out.println("Plantilla: ");
            equipo.getPlantilla().forEach(jugador ->
                System.out.println("- " + jugador.getNombre() + " (" + jugador.getPosicion() + ")")
            );
        }

        simularJornadas(competicion, equipos);
        simularAltasYBajas(equipos, em);

    }

    private static void simularJornadas(Competicion competicion, List<Equipo> equipos) {
        int jornadas = competicion.getJornadas();
        System.out.println("\nSimulando " + jornadas + " jornadas para la competición " + competicion.getNombre());

        if (equipos == null || equipos.isEmpty()) {
            System.out.println("Error: No hay equipos para simular.");
            return;
        }

        if (equipos.size() == 1) {
            System.out.println("Error: Solo hay 1 equipo en la competición. Se requieren al menos 2 equipos.");
            return;
        }

        if (equipos.size() % 2 == 1) {
            System.out.println("Error: Número impar de equipos (" + equipos.size() + "). Se requieren un número par de equipos.");
            return;
        }

        // Estadísticas: [0]=victorias, [1]=derrotas
        Map<Equipo, int[]> stats = new HashMap<>();
        equipos.forEach(e -> stats.put(e, new int[]{0, 0}));

        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        for (int j = 1; j <= jornadas; j++) {
            System.out.println("Jornada " + j + ":");

            // Lista temporal para generar emparejamientos aleatorios
            List<Equipo> pool = new ArrayList<>(equipos);
            Collections.shuffle(pool, new Random(rnd.nextLong()));

            for (int i = 0; i + 1 < pool.size(); i += 2) {
                Equipo t1 = pool.get(i);
                Equipo t2 = pool.get(i + 1);

                boolean t1Gana = rnd.nextBoolean();
                if (t1Gana) {
                    stats.get(t1)[0]++;
                    stats.get(t2)[1]++;
                    System.out.println("Partido: " + t1.getNombre() + " vs " + t2.getNombre() + " -> Ganador: " + t1.getNombre());
                } else {
                    stats.get(t2)[0]++;
                    stats.get(t1)[1]++;
                    System.out.println("Partido: " + t1.getNombre() + " vs " + t2.getNombre() + " -> Ganador: " + t2.getNombre());
                }
            }
            System.out.println();
        }

        // Ordenar equipos por número de victorias
        List<Map.Entry<Equipo, int[]>> ranking = new ArrayList<>(stats.entrySet());
        ranking.sort((e1, e2) -> Integer.compare(e2.getValue()[0], e1.getValue()[0]));
        System.out.println("Clasificación final tras " + jornadas + " jornadas:");

        int pos = 1;
        for (Map.Entry<Equipo, int[]> entry : ranking) {
            Equipo equipo = entry.getKey();
            int[] record = entry.getValue();
            System.out.println(pos + ". " + equipo.getNombre() + " - " + record[0] + "W " + record[1] + "L");
            pos++;
        }

    }

//     Simula altas y bajas de jugadores, intercambiando jugadores de la misma posición entre equipos
    private static void simularAltasYBajas(List<Equipo> equipos, EntityManager em) {
        System.out.println("\n========== SIMULACIÓN DE ALTAS Y BAJAS ==========");

        if (equipos == null || equipos.size() < 2) {
            System.out.println("Error: Se requieren al menos 2 equipos para simular altas y bajas.");
            return;
        }

        EquipoDaoJpaImpl equipoDao = new EquipoDaoJpaImpl(em);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        int numIntercambios = rnd.nextInt(5, 11);
        System.out.println("Se realizarán " + numIntercambios + " intercambios de jugadores.\n");

        for (int i = 0; i < numIntercambios; i++) {
            // Recargar equipos frescos desde la BD en cada iteración
            equipos = equipoDao.findAll();

            int idx1 = rnd.nextInt(equipos.size());
            int idx2;
            do {
                idx2 = rnd.nextInt(equipos.size());
            } while (idx2 == idx1);

            Equipo equipo1 = equipos.get(idx1);
            Equipo equipo2 = equipos.get(idx2);

            if (equipo1.getPlantilla().isEmpty() || equipo2.getPlantilla().isEmpty()) {
                System.out.println("Intercambio " + (i + 1) + ": No se puede realizar (un equipo no tiene jugadores).");
                continue;
            }

            // Seleccionar una posición aleatoria del enum
            Posicion[] posiciones = Posicion.values();
            Posicion posicionIntercambio = posiciones[rnd.nextInt(posiciones.length)];

            // Obtener el jugador de esa posición en cada equipo
            Jugador jugador1 = equipo1.getJugadorPorPosicion(posicionIntercambio);
            Jugador jugador2 = equipo2.getJugadorPorPosicion(posicionIntercambio);

            if (jugador1 == null || jugador2 == null) {
                System.out.println("Intercambio " + (i + 1) + ": No se encontró jugador en posición "
                    + posicionIntercambio + " en alguno de los equipos.");
                continue;
            }


            // Realizar el intercambio
            System.out.println("Intercambio " + (i + 1) + ":");
//            System.out.println("  " + jugador1.getNombre() + " (" + jugador1.getPosicion() + ") de "
//                + equipo1.getNombre() + " -> " + equipo2.getNombre());
//            System.out.println("  " + jugador2.getNombre() + " (" + jugador2.getPosicion() + ") de "
//                + equipo2.getNombre() + " -> " + equipo1.getNombre());
            System.out.println("  " + jugador1.getNombre() + " (" + jugador1.getPosicion() + ") "
                    + equipo1.getAbreviatura() + " -> " + equipo2.getAbreviatura());
            System.out.println("  " + jugador2.getNombre() + " (" + jugador2.getPosicion() + ") "
                    + equipo2.getAbreviatura() + " -> " + equipo1.getAbreviatura());

            em.getTransaction().begin();

            try {
                equipo1.transferirJugador(jugador1, equipo2);
                equipo2.transferirJugador(jugador2, equipo1);

                em.getTransaction().commit();
//                System.out.println("== Intercambio realizado exitosamente. ==\n");

            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                System.out.println("== Error al realizar el intercambio: " + e.getMessage() + " ==\n");
            } finally {
                em.clear(); // Limpiar contexto de persistencia para evitar entidades detached
            }
        }

        // Recargar equipos para mostrar plantillas actualizadas
        equipos = new EquipoDaoJpaImpl(em).findAll();

        System.out.println("\n========== PLANTILLAS ACTUALIZADAS ==========");

        for (Equipo equipo : equipos) {
            System.out.println("\n" + equipo.getNombre() + ":");
            for (Jugador jugador : equipo.getPlantillaOrdenada()) {
                System.out.println("  - " + jugador.getNombre() + " (" + jugador.getPosicion() + ")");
            }
        }
        System.out.println("\n==============================================\n");
    }


}
