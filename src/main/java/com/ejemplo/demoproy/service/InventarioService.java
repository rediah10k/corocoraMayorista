package com.ejemplo.demoproy.service;

import com.ejemplo.demoproy.entity.Inventario;
import com.ejemplo.demoproy.entity.Producto;
import com.ejemplo.demoproy.exception.ProductoNoEncontradoException;
import com.ejemplo.demoproy.exception.InventarioNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import com.ejemplo.demoproy.repository.InventarioRepository;
import com.ejemplo.demoproy.repository.ProductoRepository;
import com.ejemplo.demoproy.exception.CodigoInvalidoException;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public Map<String, String> parsearCodigoProducto(String codigoProd) {

        if (codigoProd == null || codigoProd.trim().isEmpty()) {
            throw new CodigoInvalidoException("El código está vacío");
        }

        Map<String, String> datos = new HashMap<>();

        try {
            String[] partes = codigoProd.split(";");

            for (String parte : partes) {
                String[] keyValue = parte.split("=");
                if (keyValue.length == 2) {
                    datos.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
            validarCamposRequeridos(datos);
            return datos;

        } catch (Exception e) {
            throw new CodigoInvalidoException("Error al parsear: " + e.getMessage());
        }
    }

    private void validarCamposRequeridos(Map<String, String> datos) {
        String[] camposRequeridos = {"LOTE", "CANT", "PROD"};

        for (String campo : camposRequeridos) {
            if (!datos.containsKey(campo) || datos.get(campo).isEmpty()) {
                throw new CodigoInvalidoException("Falta el campo requerido: " + campo);
            }
        }
    }


    @Transactional
    public Long insertarInventarioInicial(Map<String, String> datos, String fechaVencimiento) {

        String codigoProducto = datos.get("PROD");
        Producto producto = productoRepository.findById(codigoProducto)
                .orElseThrow(() -> new ProductoNoEncontradoException(codigoProducto));

        Integer cantidad;
        try {
            cantidad = Integer.parseInt(datos.get("CANT"));
        } catch (NumberFormatException e) {
            throw new CodigoInvalidoException("Cantidad inválida: " + datos.get("CANT"));
        }

        Inventario inventario = Inventario.builder()
                .lote(datos.get("LOTE"))
                .cantidad(cantidad)
                .producto(producto)
                .fechaVencimiento(LocalDate.parse(fechaVencimiento, DATE_FORMATTER))
                .build();

        Inventario guardado = inventarioRepository.save(inventario);
        log.info("Inventario creado con ID: {}", guardado.getId());
        return guardado.getId();
    }


    @Transactional
    public void actualizarInventario(Long inventarioId, String fechaIngreso,
                                     Integer prioridadSalida, String tipoRotacion,
                                     String ubicacionAlmacen) {
        log.info("Actualizando inventario ID: {} con datos adicionales", inventarioId);

        Inventario inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new InventarioNoEncontradoException(inventarioId));


        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaIngreso, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new CodigoInvalidoException("Formato de fecha de ingreso inválido: " + fechaIngreso);
        }

        inventario.setFechaIngreso(fecha);
        inventario.setPrioridadSalida(prioridadSalida);
        inventario.setTipoRotacion(tipoRotacion);
        inventario.setUbicacionAlmacen(ubicacionAlmacen);

        inventarioRepository.save(inventario);
        log.info("Inventario ID: {} actualizado exitosamente", inventarioId);
    }
}