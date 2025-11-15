package com.ejemplo.demoproy.exception;

public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(String codigoProducto) {
        super("Producto no encontrado con código: " + codigoProducto);
    }
}