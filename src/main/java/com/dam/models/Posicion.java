package com.dam.models;

/**
 * Enum que representa las posiciones de los jugadores en un equipo de esports
 * @author [Daniel Redondo Castaño]
 */
public enum Posicion {
    TOP("Top"),
    JUNGLE("Jungle"),
    MID("Mid"),
    ADC("Adc"),
    SUPPORT("Support");

    private final String nombre;

    Posicion(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

