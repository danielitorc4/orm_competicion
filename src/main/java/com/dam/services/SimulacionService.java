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
            System.out.println("Equipo participante: " + equipo.getNombre());
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
        System.out.println("Simulando " + jornadas + " jornadas para la competición " + competicion.getNombre());

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
        }

        // Ordenar equipos por número de victorias
        List<Map.Entry<Equipo, int[]>> ranking = new ArrayList<>(stats.entrySet());
        ranking.sort((e1, e2) -> Integer.compare(e2.getValue()[0], e1.getValue()[0]));
        System.out.println("\nClasificación final tras " + jornadas + " jornadas:");

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

        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        int numIntercambios = rnd.nextInt(1, 6);
        System.out.println("Se realizarán " + numIntercambios + " intercambios de jugadores.\n");

        for (int i = 0; i < numIntercambios; i++) {
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

            // Crear un mapa de jugadores por posición para cada equipo
            Map<Posicion, List<Jugador>> jugadoresEq1 = agruparPorPosicion(equipo1);
            Map<Posicion, List<Jugador>> jugadoresEq2 = agruparPorPosicion(equipo2);

            // Buscar una posición común que tenga jugadores en ambos equipos
            Posicion posicionIntercambio = null;
            for (Posicion pos : Posicion.values()) {
                if (jugadoresEq1.containsKey(pos) && !jugadoresEq1.get(pos).isEmpty() &&
                    jugadoresEq2.containsKey(pos) && !jugadoresEq2.get(pos).isEmpty()) {
                    posicionIntercambio = pos;
                    break;
                }
            }

            if (posicionIntercambio == null) {
                System.out.println("Intercambio " + (i + 1) + ": No se encontró una posición compatible entre "
                    + equipo1.getNombre() + " y " + equipo2.getNombre());
                continue;
            }

            // Seleccionar un jugador aleatorio de cada equipo en esa posición
            List<Jugador> jugadores1 = jugadoresEq1.get(posicionIntercambio);
            List<Jugador> jugadores2 = jugadoresEq2.get(posicionIntercambio);

            Jugador jugador1 = jugadores1.get(rnd.nextInt(jugadores1.size()));
            Jugador jugador2 = jugadores2.get(rnd.nextInt(jugadores2.size()));

            // Realizar el intercambio
            System.out.println("Intercambio " + (i + 1) + ":");
            System.out.println("  " + jugador1.getNombre() + " (" + jugador1.getPosicion() + ") de "
                + equipo1.getNombre() + " -> " + equipo2.getNombre());
            System.out.println("  " + jugador2.getNombre() + " (" + jugador2.getPosicion() + ") de "
                + equipo2.getNombre() + " -> " + equipo1.getNombre());

            em.getTransaction().begin();

            try {
                equipo1.removeJugador(jugador1);
                equipo2.removeJugador(jugador2);

                equipo2.addJugador(jugador1);
                equipo1.addJugador(jugador2);

                em.merge(jugador1);
                em.merge(jugador2);
                em.merge(equipo1);
                em.merge(equipo2);

                em.getTransaction().commit();
                System.out.println("== Intercambio realizado exitosamente. ==\n");

            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                System.out.println("== Error al realizar el intercambio: " + e.getMessage() + " ==\n");
            }
        }

        System.out.println("========== PLANTILLAS ACTUALIZADAS ==========");
        for (Equipo equipo : equipos) {
            System.out.println("\n" + equipo.getNombre() + ":");
            equipo.getPlantilla().forEach(jugador ->
                System.out.println("  - " + jugador.getNombre() + " (" + jugador.getPosicion() + ")")
            );
        }
        System.out.println("\n==============================================\n");
    }


//     Agrupa los jugadores de un equipo por su posición
    private static Map<Posicion, List<Jugador>> agruparPorPosicion(Equipo equipo) {
        Map<Posicion, List<Jugador>> jugadoresPorPosicion = new HashMap<>();

        for (Jugador jugador : equipo.getPlantilla()) {
            Posicion posicion = jugador.getPosicion();

            if (!jugadoresPorPosicion.containsKey(posicion)) {
                jugadoresPorPosicion.put(posicion, new ArrayList<>());
            }

            jugadoresPorPosicion.get(posicion).add(jugador);
        }

        return jugadoresPorPosicion;
    }

}
