package com.banque.courant.entity;

import jakarta.persistence.*;
import com.banque.entity.*;
import java.sql.Date;

@Entity
@Table(name = "compte_courant")
public class Banque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

}