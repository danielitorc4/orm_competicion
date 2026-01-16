package com.dam.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Teams")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
