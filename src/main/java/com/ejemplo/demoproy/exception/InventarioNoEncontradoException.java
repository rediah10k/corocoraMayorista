package com.ejemplo.demoproy.exception;

public class InventarioNoEncontradoException extends RuntimeException {
    public InventarioNoEncontradoException(Long inventarioId) {
        super("Inventario no encontrado con ID: " + inventarioId);
    }
}

