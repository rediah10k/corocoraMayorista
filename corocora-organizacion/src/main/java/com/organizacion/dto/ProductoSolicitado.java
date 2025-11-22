package com.organizacion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoSolicitado {
    private  String idProducto;
    private  Integer cantidadRequerida;
    private  String lineaOriginal;
    private  boolean valido;
    private  String mensajeError;
}

