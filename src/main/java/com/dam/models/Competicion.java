package com.dam.models;

import jakarta.persistence.*;
/**
 * Esta clase se encarga de representar una competicion de esports
 * @author [Daniel Redondo Castaño]
 */
@Entity
@Table(name = "Competitions")
public class Competicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nombre;
    @Column(nullable = false)
    private String juego;
    @Column(nullable = false)
    private String region;
    @Column(nullable = false)
    private int jornadas;

    public Competicion() {
    }

    public Competicion(String nombre, String juego, String region, int jornadas) {
        this.nombre = nombre;
        this.juego = juego;
        this.region = region;
        this.jornadas = jornadas;
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

    public String getJuego() {
        return juego;
    }

    public void setJuego(String juego) {
        this.juego = juego;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getJornadas() {
        return jornadas;
    }

    public void setJornadas(int jornadas) {
        this.jornadas = jornadas;
    }

    @Override
    public String toString() {
        return "Competicion{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", juego='" + juego + '\'' +
                ", region='" + region + '\'' +
                ", jornadas=" + jornadas +
                '}';
    }
}
