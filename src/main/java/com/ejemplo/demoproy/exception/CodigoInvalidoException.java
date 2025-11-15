package com.ejemplo.demoproy.exception;

public class CodigoInvalidoException extends RuntimeException {
    public CodigoInvalidoException(String codigoProducto) {
        super("Producto no encontrado con código: " + codigoProducto);
    }
}