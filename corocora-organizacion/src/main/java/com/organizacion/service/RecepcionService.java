package com.organizacion.service;

import com.organizacion.dto.ProductoSolicitado;
import com.organizacion.dto.RecepcionActualizacionRequest;
import com.organizacion.dto.RecepcionRequest;

import java.util.List;
import java.util.Map;

public interface RecepcionService {

    Long insertarRecepcion(RecepcionRequest recepcionRequest);

    void actualizarInventario(RecepcionActualizacionRequest recepcionActualizacionRequest);



}
