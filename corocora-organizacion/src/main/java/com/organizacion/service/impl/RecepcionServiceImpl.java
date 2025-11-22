package com.organizacion.service.impl;

import com.organizacion.dto.ProductoSolicitado;
import com.organizacion.dto.RecepcionActualizacionRequest;
import com.organizacion.dto.RecepcionRequest;
import com.organizacion.enitity.Producto;
import com.organizacion.enitity.Recepcion;
import com.organizacion.exception.ProductoNoEncontradoException;
import com.organizacion.exception.InventarioNoEncontradoException;
import com.organizacion.repository.RecepcionRepository;
import com.organizacion.service.RecepcionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.organizacion.repository.ProductoRepository;
import com.organizacion.exception.CodigoInvalidoException;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecepcionServiceImpl implements RecepcionService {

    private final RecepcionRepository recepcionRepository;
    private final ProductoRepository productoRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");



    @Override
    @Transactional
    public Long insertarRecepcion(RecepcionRequest recepcionRequest) {

        String codigoProducto = recepcionRequest.getProducto();
        Producto producto = productoRepository.findById(codigoProducto)
                .orElseThrow(() -> new ProductoNoEncontradoException(codigoProducto));

        Integer cantidad;
        try {
            cantidad = recepcionRequest.getCantidad();
        } catch (NumberFormatException e) {
            throw new CodigoInvalidoException("Cantidad inválida: " + recepcionRequest.getCantidad());
        }

        Recepcion recepcion = Recepcion.builder()
                .lote(recepcionRequest.getLote())
                .cantidad(cantidad)
                .producto(producto)
                .fechaVencimiento(LocalDate.parse(recepcionRequest.getFechaVencimiento(), DATE_FORMATTER))
                .build();

        Recepcion guardado = recepcionRepository.save(recepcion);
        return guardado.getId();
    }

    @Override
    @Transactional
    public void actualizarInventario(RecepcionActualizacionRequest recepcionActualizacionRequest) {

        Recepcion Recepcion = recepcionRepository.findById(recepcionActualizacionRequest.getRecepcionId())
                .orElseThrow(() -> new InventarioNoEncontradoException(recepcionActualizacionRequest.getRecepcionId()));


        LocalDate fecha;
        try {
            fecha = LocalDate.parse(recepcionActualizacionRequest.getFechaIngreso(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new CodigoInvalidoException("Formato de fecha de ingreso inválido: " + recepcionActualizacionRequest.getFechaIngreso());
        }

        Recepcion.setFechaIngreso(fecha);
        Recepcion.setPrioridadSalida(recepcionActualizacionRequest.getPrioridadSalida());
        Recepcion.setTipoRotacion(recepcionActualizacionRequest.getTipoRotacion());
        Recepcion.setUbicacionAlmacen(recepcionActualizacionRequest.getUbicacionAlmacen());

        recepcionRepository.save(Recepcion);

    }




}