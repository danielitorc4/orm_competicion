package com.dam.models;

import jakarta.persistence.*;

/**
 * Esta clase se encarga de representar un jugador de un equipo de esports
 * @author [Daniel Redondo Castaño]
 */
@Entity
@Table(name= "Players")
@NamedQuery(name = "Jugador.findNuevasIncorporaciones",
    query = "SELECT j FROM Jugador j WHERE j.nuevaIncorporacion = true")
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "name")
    private String nombre;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "position")
    private Posicion posicion;
    @Column(nullable = false, name = "age")
    private int edad;
    @Column(nullable = false, name = "nationality")
    private String nacionalidad;
    @Column(name = "nueva_incorporacion")
    private boolean nuevaIncorporacion = false;
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Equipo equipo;

    public Jugador() {
    }

    public Jugador(String nombre, Posicion posicion, Equipo equipo) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.equipo = equipo;
        this.edad = 20;
        this.nacionalidad = "Desconocida";
    }

    public Jugador(String nombre, Posicion posicion, int edad, String nacionalidad, Equipo equipo) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.edad = edad;
        this.nacionalidad = nacionalidad;
        this.equipo = equipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public boolean isNuevaIncorporacion() {
        return nuevaIncorporacion;
    }

    public void setNuevaIncorporacion(boolean nuevaIncorporacion) {
        this.nuevaIncorporacion = nuevaIncorporacion;
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", posicion='" + posicion + '\'' +
                ", equipo=" + equipo +
                '}';
    }
}
