package com.dam.services;

import com.dam.dao.jpa.EquipoDaoJpaImpl;
import com.dam.models.Competicion;
import com.dam.models.Equipo;
import jakarta.persistence.EntityManager;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
//        simularAltasYBajas(competicion, equipos);

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

            // Emparejar por pares en la lista mezclada
            for (int i = 0; i + 1 < pool.size(); i += 2) {
                Equipo t1 = pool.get(i);
                Equipo t2 = pool.get(i + 1);

                // Elegir ganador aleatorio entre los dos
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

        // Mostrar resultados finales
        int pos = 1;
        for (Map.Entry<Equipo, int[]> entry : ranking) {
            Equipo equipo = entry.getKey();
            int[] record = entry.getValue();
            System.out.println(pos + ". " + equipo.getNombre() + " - " + record[0] + "W " + record[1] + "L");
            pos++;
        }

    }

}
