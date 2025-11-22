package com.organizacion.exception;

public class InventarioNoEncontradoException extends RuntimeException {
    public InventarioNoEncontradoException(Long inventarioId) {
        super("Inventario no encontrado con ID: " + inventarioId);
    }
}

