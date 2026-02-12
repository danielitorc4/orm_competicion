package com.dam.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Esta clase representa un fichaje/transferencia entre equipos
 * @author [Daniel Redondo Castaño]
 */
@Entity
@Table(name = "Transfers")
public class Fichaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Jugador jugador;

    @ManyToOne
    @JoinColumn(name = "from_team_id", nullable = false)
    private Equipo equipoOrigen;

    @ManyToOne
    @JoinColumn(name = "to_team_id", nullable = false)
    private Equipo equipoDestino;

    @Column(name = "transfer_date", nullable = false)
    private LocalDateTime fechaFichaje;

    public Fichaje() {
    }

    public Fichaje(Jugador jugador, Equipo equipoOrigen, Equipo equipoDestino) {
        this.jugador = jugador;
        this.equipoOrigen = equipoOrigen;
        this.equipoDestino = equipoDestino;
        this.fechaFichaje = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public Equipo getEquipoOrigen() {
        return equipoOrigen;
    }

    public void setEquipoOrigen(Equipo equipoOrigen) {
        this.equipoOrigen = equipoOrigen;
    }

    public Equipo getEquipoDestino() {
        return equipoDestino;
    }

    public void setEquipoDestino(Equipo equipoDestino) {
        this.equipoDestino = equipoDestino;
    }

    public LocalDateTime getFechaFichaje() {
        return fechaFichaje;
    }

    public void setFechaFichaje(LocalDateTime fechaFichaje) {
        this.fechaFichaje = fechaFichaje;
    }

    @Override
    public String toString() {
        return "Fichaje{" +
                "id=" + id +
                ", jugador=" + (jugador != null ? jugador.getNombre() : "null") +
                ", equipoOrigen=" + (equipoOrigen != null ? equipoOrigen.getNombre() : "null") +
                ", equipoDestino=" + (equipoDestino != null ? equipoDestino.getNombre() : "null") +
                ", fechaFichaje=" + fechaFichaje +
                '}';
    }
}

