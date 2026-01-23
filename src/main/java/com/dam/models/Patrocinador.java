package com.dam.models;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase se encarga de representar un patrocinador de equipos de esports
 * @author [Daniel Redondo Castaño]
 */
@Entity
@Table(name = "Sponsors")
public class Patrocinador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, name = "name")
    private String nombre;
    @ManyToMany(mappedBy = "patrocinadores")
    private List<Equipo> equipos = new ArrayList<>();

    public Patrocinador() {
    }

    public Patrocinador(String nombre) {
        this.nombre = nombre;
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

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
    }

    @Override
    public String toString() {
        return "Patrocinador{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", equipos=" + equipos +
                '}';
    }
}
