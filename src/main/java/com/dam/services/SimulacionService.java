package com.dam.services;

import com.dam.dao.jpa.EquipoDaoJpaImpl;
import com.dam.dao.jpa.FichajeDaoJpaImpl;
import com.dam.models.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/*
* Esta clase se encarga de simular el matchmaking entre equipo y las altas y bajas
* Simula la competición entera, validando que se cumplan los requisitos
* @author [Daniel Redondo Castaño]
 */
public class SimulacionService {

    // Almacenar clasificaciones en diferentes momentos
    private static Map<Equipo, int[]> clasificacionInicial;
    private static Map<Equipo, int[]> clasificacionMitad;
    private static Map<Equipo, int[]> clasificacionFinal;

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
        realizarConsultas(competicion, em);

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

        // Guardar clasificación inicial (antes de empezar)
        clasificacionInicial = copiarStats(stats);

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int mitadJornadas = jornadas / 2;

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

            // Guardar clasificación a mitad de temporada
            if (j == mitadJornadas) {
                clasificacionMitad = copiarStats(stats);
            }
        }

        // Guardar clasificación final
        clasificacionFinal = copiarStats(stats);

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

    private static Map<Equipo, int[]> copiarStats(Map<Equipo, int[]> original) {
        Map<Equipo, int[]> copia = new HashMap<>();
        for (Map.Entry<Equipo, int[]> entry : original.entrySet()) {
            copia.put(entry.getKey(), new int[]{entry.getValue()[0], entry.getValue()[1]});
        }
        return copia;
    }

    private static void simularAltasYBajas(List<Equipo> equipos, EntityManager em) {
        System.out.println("\n========== SIMULACIÓN DE ALTAS Y BAJAS ==========");

        if (equipos == null || equipos.size() < 2) {
            System.out.println("Error: Se requieren al menos 2 equipos para simular altas y bajas.");
            return;
        }

        EquipoDaoJpaImpl equipoDao = new EquipoDaoJpaImpl(em);
        FichajeDaoJpaImpl fichajeDao = new FichajeDaoJpaImpl(em);
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
            System.out.println("  " + jugador1.getNombre() + " (" + jugador1.getPosicion() + ") "
                    + equipo1.getAbreviatura() + " -> " + equipo2.getAbreviatura());
            System.out.println("  " + jugador2.getNombre() + " (" + jugador2.getPosicion() + ") "
                    + equipo2.getAbreviatura() + " -> " + equipo1.getAbreviatura());

            em.getTransaction().begin();

            try {
                // Marcar jugadores como nuevas incorporaciones
                jugador1.setNuevaIncorporacion(true);
                jugador2.setNuevaIncorporacion(true);

                // Guardar equipos originales antes de la transferencia
                Equipo equipoOrigen1 = equipo1;
                Equipo equipoOrigen2 = equipo2;

                equipo1.transferirJugador(jugador1, equipo2);
                equipo2.transferirJugador(jugador2, equipo1);

                // Registrar los fichajes
                Fichaje fichaje1 = new Fichaje(jugador1, equipoOrigen1, equipo2);
                Fichaje fichaje2 = new Fichaje(jugador2, equipoOrigen2, equipo1);
                fichajeDao.save(fichaje1);
                fichajeDao.save(fichaje2);

                em.getTransaction().commit();

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

    private static void realizarConsultas(Competicion competicion, EntityManager em) {
        System.out.println("\n==================== CONSULTAS JPQL/HQL ====================\n");

        // 1. Consulta nativa para obtener características de la competición
        consulta1CaracteristicasCompeticion(competicion, em);

        // 2. Consulta todos los equipos participantes
        consulta2EquiposParticipantes(em);

        // 3. Lista de deportistas de un equipo específico
        consulta3DeportistasEquipo(em);

        // 4. Patrocinadores de un equipo concreto
        consulta4PatrocinadoresEquipo(em);

        // 5. Deportistas y patrocinadores de un equipo específico
        consulta5DeportistasYPatrocinadores(em);

        // 6. Edad promedio de deportistas de un equipo
        consulta6EdadPromedioEquipo(em);

        // 7. Deportistas mayores de 23 años agrupados por nacionalidad
        consulta7DeportistasMayores23PorNacionalidad(em);

        // 8. Clasificación al inicio, mitad y final de temporada
        consulta8ClasificacionesMomentos();

        // 9. Los tres equipos con más y menos puntos
        consulta9TopYBottomEquipos();

        // 10. Nuevas incorporaciones (NamedQuery)
        consulta10NuevasIncorporaciones(em);

        // 11. Fichajes realizados entre equipos
        consulta11FichajesRealizados(em);

        // 12. Total de deportistas en la competición
        consulta12TotalDeportistas(em);

        System.out.println("\n=============================================================\n");
    }

    private static void consulta1CaracteristicasCompeticion(Competicion competicion, EntityManager em) {
        System.out.println("=== CONSULTA 1: Características de la competición ===");

        Query nativeQuery = em.createNativeQuery(
            "SELECT id, name, game, region, matches FROM Competitions WHERE id = ?");
        nativeQuery.setParameter(1, competicion.getId());

        @SuppressWarnings("unchecked")
        List<Object[]> results = nativeQuery.getResultList();

        for (Object[] row : results) {
            System.out.println("  ID: " + row[0]);
            System.out.println("  Nombre: " + row[1]);
            System.out.println("  Juego: " + row[2]);
            System.out.println("  Región: " + row[3]);
            System.out.println("  Jornadas: " + row[4]);
        }
        System.out.println();
    }

    private static void consulta2EquiposParticipantes(EntityManager em) {
        System.out.println("=== CONSULTA 2: Equipos participantes en la competición ===");

        TypedQuery<Equipo> query = em.createQuery(
            "SELECT e FROM Equipo e ORDER BY e.nombre", Equipo.class);
        List<Equipo> equipos = query.getResultList();

        for (Equipo equipo : equipos) {
            System.out.println("  - " + equipo.getNombre() + " (" + equipo.getAbreviatura() + ")");
        }
        System.out.println("  Total equipos: " + equipos.size());
        System.out.println();
    }

    private static void consulta3DeportistasEquipo(EntityManager em) {
        System.out.println("=== CONSULTA 3: Deportistas de un equipo específico (G2 Esports) ===");

        TypedQuery<Jugador> query = em.createQuery(
            "SELECT j FROM Jugador j WHERE j.equipo.nombre = :nombreEquipo ORDER BY j.posicion",
            Jugador.class);
        query.setParameter("nombreEquipo", "G2 Esports");

        List<Jugador> jugadores = query.getResultList();
        for (Jugador j : jugadores) {
            System.out.println("  - " + j.getNombre() + " (" + j.getPosicion() + ") - "
                + j.getEdad() + " años - " + j.getNacionalidad());
        }
        System.out.println();
    }

    private static void consulta4PatrocinadoresEquipo(EntityManager em) {
        System.out.println("=== CONSULTA 4: Patrocinadores de un equipo concreto (Fnatic) ===");

        TypedQuery<Patrocinador> query = em.createQuery(
            "SELECT p FROM Patrocinador p JOIN p.equipos e WHERE e.nombre = :nombreEquipo",
            Patrocinador.class);
        query.setParameter("nombreEquipo", "Fnatic");

        List<Patrocinador> patrocinadores = query.getResultList();
        for (Patrocinador p : patrocinadores) {
            System.out.println("  - " + p.getNombre());
        }
        System.out.println();
    }

    private static void consulta5DeportistasYPatrocinadores(EntityManager em) {
        System.out.println("=== CONSULTA 5: Deportistas y patrocinadores de un equipo (Movistar KOI) ===");

        // Obtener equipo con sus jugadores y patrocinadores en una sola consulta
        TypedQuery<Equipo> query = em.createQuery(
            "SELECT DISTINCT e FROM Equipo e " +
            "LEFT JOIN FETCH e.plantilla " +
            "LEFT JOIN FETCH e.patrocinadores " +
            "WHERE e.nombre = :nombreEquipo", Equipo.class);
        query.setParameter("nombreEquipo", "Movistar KOI");

        Equipo equipo = query.getResultStream().findFirst().orElse(null);

        if (equipo != null) {
            System.out.println("  Deportistas:");
            equipo.getPlantillaOrdenada().forEach(j ->
                System.out.println("    - " + j.getNombre() + " (" + j.getPosicion() + ")"));

            System.out.println("  Patrocinadores:");
            equipo.getPatrocinadores().forEach(p ->
                System.out.println("    - " + p.getNombre()));
        } else {
            System.out.println("  Equipo no encontrado.");
        }
        System.out.println();
    }

    private static void consulta6EdadPromedioEquipo(EntityManager em) {
        System.out.println("=== CONSULTA 6: Edad promedio de deportistas de un equipo (Team Heretics) ===");

        TypedQuery<Double> query = em.createQuery(
            "SELECT AVG(j.edad) FROM Jugador j WHERE j.equipo.nombre = :nombreEquipo",
            Double.class);
        query.setParameter("nombreEquipo", "Team Heretics");

        Double edadPromedio = query.getSingleResult();
        if (edadPromedio != null) {
            System.out.println("  Edad promedio: " + String.format("%.2f", edadPromedio) + " años");
        } else {
            System.out.println("  No hay jugadores en el equipo.");
        }
        System.out.println();
    }

    private static void consulta7DeportistasMayores23PorNacionalidad(EntityManager em) {
        System.out.println("=== CONSULTA 7: Deportistas mayores de 23 años por nacionalidad ===");

        TypedQuery<Object[]> query = em.createQuery(
            "SELECT j.nacionalidad, COUNT(j) FROM Jugador j WHERE j.edad > 23 " +
            "GROUP BY j.nacionalidad ORDER BY COUNT(j) DESC",
            Object[].class);

        List<Object[]> results = query.getResultList();
        results.forEach(row ->
            System.out.println("  " + row[0] + ": " + row[1] + " deportista(s)"));

        long total = results.stream().mapToLong(row -> (Long) row[1]).sum();
        System.out.println("  Total deportistas mayores de 23: " + total);
        System.out.println();
    }

    private static void consulta8ClasificacionesMomentos() {
        System.out.println("=== CONSULTA 8: Clasificación al inicio, mitad y final de temporada ===");

        System.out.println("\n  --- CLASIFICACIÓN INICIAL (Inicio de temporada) ---");
        mostrarClasificacion(clasificacionInicial);

        System.out.println("\n  --- CLASIFICACIÓN A MITAD DE TEMPORADA ---");
        mostrarClasificacion(clasificacionMitad);

        System.out.println("\n  --- CLASIFICACIÓN FINAL ---");
        mostrarClasificacion(clasificacionFinal);
        System.out.println();
    }

    private static void mostrarClasificacion(Map<Equipo, int[]> stats) {
        if (stats == null || stats.isEmpty()) {
            System.out.println("    No hay datos de clasificación disponibles.");
            return;
        }

        List<Map.Entry<Equipo, int[]>> ranking = new ArrayList<>(stats.entrySet());
        ranking.sort((e1, e2) -> Integer.compare(e2.getValue()[0], e1.getValue()[0]));

        int pos = 1;
        for (Map.Entry<Equipo, int[]> entry : ranking) {
            Equipo equipo = entry.getKey();
            int[] record = entry.getValue();
            System.out.println("    " + pos + ". " + equipo.getNombre() + " - " + record[0] + "W " + record[1] + "L");
            pos++;
        }
    }

    private static void consulta9TopYBottomEquipos() {
        System.out.println("=== CONSULTA 9: Top 3 y Bottom 3 equipos ===");

        if (clasificacionFinal == null || clasificacionFinal.isEmpty()) {
            System.out.println("  No hay datos de clasificación disponibles.");
            return;
        }

        List<Map.Entry<Equipo, int[]>> ranking = new ArrayList<>(clasificacionFinal.entrySet());
        ranking.sort((e1, e2) -> Integer.compare(e2.getValue()[0], e1.getValue()[0]));

        System.out.println("\n  TOP 3 equipos con más victorias:");
        int limit = Math.min(3, ranking.size());
        for (int i = 0; i < limit; i++) {
            Equipo equipo = ranking.get(i).getKey();
            int[] record = ranking.get(i).getValue();
            System.out.println("    " + (i + 1) + ". " + equipo.getNombre() + " - " + record[0] + "W " + record[1] + "L");
        }

        System.out.println("\n  BOTTOM 3 equipos con menos victorias:");
        int start = Math.max(0, ranking.size() - 3);
        int pos = 1;
        for (int i = ranking.size() - 1; i >= start; i--) {
            Equipo equipo = ranking.get(i).getKey();
            int[] record = ranking.get(i).getValue();
            System.out.println("    " + pos + ". " + equipo.getNombre() + " - " + record[0] + "W " + record[1] + "L");
            pos++;
        }
        System.out.println();
    }

    private static void consulta10NuevasIncorporaciones(EntityManager em) {
        System.out.println("=== CONSULTA 10: Nuevas incorporaciones (NamedQuery) ===");

        TypedQuery<Jugador> query = em.createNamedQuery("Jugador.findNuevasIncorporaciones", Jugador.class);
        List<Jugador> nuevasIncorporaciones = query.getResultList();

        if (nuevasIncorporaciones.isEmpty()) {
            System.out.println("  No hay nuevas incorporaciones registradas.");
        } else {
            for (Jugador j : nuevasIncorporaciones) {
                System.out.println("  - " + j.getNombre() + " (" + j.getPosicion() + ") -> "
                    + (j.getEquipo() != null ? j.getEquipo().getNombre() : "Sin equipo"));
            }
            System.out.println("  Total nuevas incorporaciones: " + nuevasIncorporaciones.size());
        }
        System.out.println();
    }

    private static void consulta11FichajesRealizados(EntityManager em) {
        System.out.println("=== CONSULTA 11: Fichajes realizados entre equipos ===");

        TypedQuery<Fichaje> query = em.createQuery(
            "SELECT f FROM Fichaje f ORDER BY f.fechaFichaje", Fichaje.class);
        List<Fichaje> fichajes = query.getResultList();

        if (fichajes.isEmpty()) {
            System.out.println("  No hay fichajes registrados.");
        } else {
            for (Fichaje f : fichajes) {
                System.out.println("  - " + f.getJugador().getNombre() + ": "
                    + f.getEquipoOrigen().getAbreviatura() + " -> "
                    + f.getEquipoDestino().getAbreviatura()
                    + " (" + f.getFechaFichaje().toLocalDate() + ")");
            }
            System.out.println("  Total fichajes: " + fichajes.size());
        }
        System.out.println();
    }

    private static void consulta12TotalDeportistas(EntityManager em) {
        System.out.println("=== CONSULTA 12: Total de deportistas en la competición ===");

        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(j) FROM Jugador j", Long.class);
        Long total = query.getSingleResult();

        System.out.println("  Total de deportistas: " + total);
        System.out.println();
    }
}
