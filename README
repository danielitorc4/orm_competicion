# Práctica UD3 - Simulación de Competición de Esports

## Descripción

Aplicación que simula una competición de esports (League of Legends - LEC) utilizando JPA/Hibernate para la persistencia de datos. 
Permite gestionar equipos, jugadores, patrocinadores y fichajes, además de realizar consultas JPQL y nativas sobre la base de datos.

**Versión:** V1

## Estructura del Proyecto

```
src/main/java/com/dam/
├── SimulacionMain.java      # Punto de entrada de la aplicación
├── dao/                     # Capa de acceso a datos
│   ├── iGenericDao.java     # Interfaz genérica CRUD
│   ├── i*Dao.java           # Interfaces específicas por entidad
│   └── jpa/                 # Implementaciones JPA
│       └── *DaoJpaImpl.java # Implementaciones con EntityManager
├── models/                  # Capa de entidades/modelos
│   ├── Competicion.java     # Entidad competición
│   ├── Equipo.java          # Entidad equipo
│   ├── Jugador.java         # Entidad jugador
│   ├── Ciudad.java          # Entidad ciudad
│   ├── Patrocinador.java    # Entidad patrocinador
│   ├── Fichaje.java         # Entidad fichaje/transferencia
│   └── Posicion.java        # Enum posiciones de jugadores
├── services/                # Capa de lógica de negocio
│   ├── DataLoaderService.java    # Carga de datos desde CSV
│   └── SimulacionService.java    # Simulación y consultas JPQL
└── util/
    └── JpaUtil.java         # Utilidad para EntityManagerFactory
```

### Responsabilidades por Capa

| Capa | Responsabilidad |
|------|-----------------|
| **dao/** | Operaciones CRUD y acceso a base de datos |
| **models/** | Entidades JPA mapeadas a tablas MySQL |
| **services/** | Lógica de simulación, carga de datos y consultas |
| **util/** | Gestión del ciclo de vida de JPA |

