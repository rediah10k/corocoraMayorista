package com.bpm.engine.pedidos.util;

import com.bpm.engine.pedidos.dto.ProductoSolicitado;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class PedidosUtil {

    public List<ProductoSolicitado> parsearProductosSolicitados(String productos) {
        List<ProductoSolicitado> productosSolicitados = new ArrayList<>();
        String[] lineas = productos.split("\n");
        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) {
                continue;
            }
            try {
                String[] partes = linea.split(",");
                if (partes.length != 2) {
                    productosSolicitados.add(new ProductoSolicitado(linea, "Formato incorrecto"));
                    continue;
                }

                String idProducto = partes[0].trim();
                Integer cantidadRequerida;

                try {
                    cantidadRequerida = Integer.parseInt(partes[1].trim());
                } catch (NumberFormatException e) {

                    productosSolicitados.add(new ProductoSolicitado(linea, "Cantidad inválida"));
                    continue;
                }

                productosSolicitados.add(new ProductoSolicitado(idProducto, cantidadRequerida, linea));

            } catch (Exception e) {
                productosSolicitados.add(new ProductoSolicitado(linea, "Error procesando línea"));
            }
        }
        return productosSolicitados;
    }


    /**
     * Parsea un String a LocalDate soportando múltiples formatos
     */
    public LocalDate parsearFechaString(String fechaStr) {
        try {

            if (fechaStr.contains("T")) {
                String soloFecha = fechaStr.substring(0, fechaStr.indexOf('T'));
                return LocalDate.parse(soloFecha);
            }

            return LocalDate.parse(fechaStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha inválido: " + fechaStr +
                    ". Use formato yyyy-MM-dd o yyyy-MM-ddTHH:mm (ejemplo: 2025-01-20 o 2025-11-20T14:00-05:00)");
        }
    }
}
