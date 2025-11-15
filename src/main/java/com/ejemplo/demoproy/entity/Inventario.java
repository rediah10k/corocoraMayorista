package com.ejemplo.demoproy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "INVENTARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "LOTE", nullable = false, length = 50)
    private String lote;

    @Column(name = "FECHA_VENCIMIENTO", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "CANTIDAD", nullable = false)
    private Integer cantidad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCTO_ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_INVENTARIO_PRODUCTO"))
    private Producto producto;


    @Column(name = "FECHA_INGRESO")
    private LocalDate fechaIngreso;

    @Column(name = "PRIORIDAD_SALIDA")
    private Integer prioridadSalida;

    @Column(name = "TIPO_ROTACION", length = 50)
    private String tipoRotacion;

    @Column(name = "UBICACION_ALMACEN", length = 100)
    private String ubicacionAlmacen;

    @Column(name = "FECHA_REGISTRO", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}