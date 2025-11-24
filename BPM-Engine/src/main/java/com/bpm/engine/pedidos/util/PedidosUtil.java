package com.bpm.engine.pedidos.util;

import com.bpm.engine.pedidos.dto.ProductoSolicitado;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component("pedidosUtil")
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


    public Integer calcularTotalPedido(String productos) {
        if (productos == null || productos.trim().isEmpty()) {
            return 0;
        }

        int total = 0;
        String[] lineas = productos.split(";");

        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) {
                continue;
            }

            try {
                String[] partes = linea.split(",");
                if (partes.length >= 5) {

                    String valorStr = partes[4].trim();
                    int valor = Integer.parseInt(valorStr);
                    total += valor;
                }
            } catch (NumberFormatException e) {

                System.err.println("Error parseando línea: " + linea);
            }
        }

        return total;
    }

    public Integer calcularPrecioSinIVA(String productos) {
        Integer total = calcularTotalPedido(productos);
        return (int) Math.round(total * 0.81);
    }
}


