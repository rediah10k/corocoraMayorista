package com.organizacion.enitity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRODUCTOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "NOMBRE", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "PRECIO", nullable = false, unique = false)
    private Integer precio;

    @Column(name = "CANTIDAD", nullable = false, unique = false)
    private String cantidad;
}