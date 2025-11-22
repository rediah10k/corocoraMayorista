package com.bpm.engine.pedidos.dto;

import lombok.Getter;

import java.io.Serializable;


@Getter
public class ProductoSolicitado implements Serializable {
    private String idProducto;
    private Integer cantidadRequerida;
    private String lineaOriginal;
    private boolean valido;
    private String mensajeError;

    public  ProductoSolicitado(){}

    public ProductoSolicitado(String idProducto, Integer cantidadRequerida, String lineaOriginal) {
        this.idProducto = idProducto;
        this.cantidadRequerida = cantidadRequerida;
        this.lineaOriginal = lineaOriginal;
        this.valido = true;
        this.mensajeError = null;
    }


    public ProductoSolicitado(String lineaOriginal, String mensajeError) {
        this.lineaOriginal = lineaOriginal;
        this.mensajeError = mensajeError;
        this.valido = false;
        this.idProducto = null;
        this.cantidadRequerida = null;
    }
}

