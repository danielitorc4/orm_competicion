package com.dam.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase se encarga de representar un equipo de esports con su plantilla y patrocinadores
 * @author [Daniel Redondo Castaño]
 */
@Entity
@Table(name = "Teams")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nombre;
    @Column(nullable = false, length = 5)
    private String abreviatura;
    @Column
    private LocalDate fechaFundacion;
    @Column
    private Ciudad ciudad;
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Jugador> plantilla = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "Team_Sponsor",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "sponsor_id")
    )
    private List<Patrocinador> patrocinadores = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competicion competicion;

    public Equipo() {
    }

    public Equipo(String nombre, String abreviatura, Ciudad ciudad) {
        this.nombre = nombre;
        this.abreviatura = abreviatura;
        this.fechaFundacion = LocalDate.now();
        this.ciudad = ciudad;
    }

    public void addJugador(Jugador jugador) {
        plantilla.add(jugador);
        jugador.setEquipo(this);
    }

    public void removeJugador(Jugador jugador) {
        plantilla.remove(jugador);
        jugador.setEquipo(null);
    }

    public void addPatrocinador (Patrocinador patrocinador) {
        patrocinadores.add(patrocinador);
        patrocinador.getEquipos().add(this);
    }

    public void removePatrocinador (Patrocinador patrocinador) {
        patrocinadores.remove(patrocinador);
        patrocinador.getEquipos().remove(this);
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

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public LocalDate getFechaFundacion() {
        return fechaFundacion;
    }

    public void setFechaFundacion(LocalDate fechaFundacion) {
        this.fechaFundacion = fechaFundacion;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public List<Jugador> getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(List<Jugador> plantilla) {
        this.plantilla = plantilla;
    }

    public List<Patrocinador> getPatrocinadores() {
        return patrocinadores;
    }

    public void setPatrocinadores(List<Patrocinador> patrocinadores) {
        this.patrocinadores = patrocinadores;
    }

    public Competicion getCompeticion() {
        return competicion;
    }

    public void setCompeticion(Competicion competicion) {
        this.competicion = competicion;
    }

    @Override
    public String toString() {
        return "Equipo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", abreviatura='" + abreviatura + '\'' +
                ", fechaFundacion=" + fechaFundacion +
                ", ciudad=" + ciudad +
                ", plantilla=" + plantilla +
                ", patrocinadores=" + patrocinadores +
                ", competicion=" + competicion +
                '}';
    }
}
