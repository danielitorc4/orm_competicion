package com.dam.models;

import jakarta.persistence.*;

/**
 * Esta clase se encarga de representar un jugador de un equipo de esports
 * @author [Daniel Redondo Castaño]
 */
@Entity
@Table(name= "Players")
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "name")
    private String nombre;
    @Column(nullable = false, name = "position")
    private String posicion;
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Equipo equipo;

    public Jugador() {
    }

    public Jugador(String nombre, String posicion, Equipo equipo) {
        this.nombre = nombre;
        this.posicion = posicion;
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

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
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
