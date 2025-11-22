package com.organizacion.service.impl;

import com.organizacion.dto.*;
import com.organizacion.enitity.Cliente;
import com.organizacion.enitity.ItemPedido;
import com.organizacion.enitity.Pedido;
import com.organizacion.enitity.Producto;
import com.organizacion.repository.ClienteRepository;
import com.organizacion.repository.ItemPedidoRepository;
import com.organizacion.repository.PedidoRepository;
import com.organizacion.repository.ProductoRepository;
import com.organizacion.service.PedidosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PedidosServiceImpl implements PedidosService {

    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> validarStockProductos(List<ProductoSolicitado> productosSolicitados) {

        Map<String, Producto> productosDB = obtenerProductosDesdeDB(productosSolicitados);

        return compararStockConBD(productosSolicitados, productosDB);
    }



    private Map<String, Producto> obtenerProductosDesdeDB(List<ProductoSolicitado> productosSolicitados) {
        Map<String, Producto> productosDB = new HashMap<>();

        for (ProductoSolicitado solicitado : productosSolicitados) {
            if (!solicitado.isValido()) {
                continue;
            }

            String idProducto = solicitado.getIdProducto();
            Producto producto = productoRepository.findById(idProducto).orElse(null);

            if (producto != null) {
                productosDB.put(idProducto, producto);
            }
        }

        return productosDB;
    }


    private Map<String, Object> compararStockConBD(List<ProductoSolicitado> productosSolicitados,
                                                   Map<String, Producto> productosDB) {

        Map<String, Object> resultado = new HashMap<>();
        StringBuilder stockFaltante = new StringBuilder();
        boolean todosDisponibles = true;

        for (ProductoSolicitado solicitado : productosSolicitados) {

            if (!solicitado.isValido()) {
                todosDisponibles = false;
                if (stockFaltante.length() > 0) {
                    stockFaltante.append("; ");
                }
                stockFaltante.append(solicitado.getMensajeError())
                        .append(": ").append(solicitado.getLineaOriginal());
                continue;
            }

            String idProducto = solicitado.getIdProducto();
            Integer cantidadRequerida = solicitado.getCantidadRequerida();

            Producto producto = productosDB.get(idProducto);
            if (producto == null) {
                todosDisponibles = false;
                if (stockFaltante.length() > 0) {
                    stockFaltante.append("; ");
                }
                stockFaltante.append("Producto no encontrado: ").append(idProducto);
                continue;
            }

            Integer cantidadDisponible;
            try {
                cantidadDisponible = Integer.parseInt(producto.getCantidad());
            } catch (NumberFormatException e) {

                todosDisponibles = false;
                if (stockFaltante.length() > 0) {
                    stockFaltante.append("; ");
                }
                continue;
            }

            if (cantidadDisponible < cantidadRequerida) {
                todosDisponibles = false;
                if (!stockFaltante.isEmpty()) {
                    stockFaltante.append("; ");
                }
                stockFaltante.append(idProducto)
                        .append(" (disponible: ").append(cantidadDisponible)
                        .append(", requerido: ").append(cantidadRequerida).append(")");
            }
        }

        resultado.put("stock", todosDisponibles);
        resultado.put("stockFaltante", !stockFaltante.isEmpty() ? stockFaltante.toString() : "");

        return resultado;
    }

    @Override
    public PedidoResponse crearPedido(PedidoRequest pedidoRequest){
        Cliente cliente = clienteRepository.findById(pedidoRequest.getClienteId()).orElse(null);

        if (cliente != null) {
            Pedido pedido = pedidoRepository.save(
                    Pedido.builder()
                            .cliente(cliente)
                            .estado(pedidoRequest.getEstado())
                            .build()
            );

            return PedidoResponse.builder()
                    .pedidoId(pedido.getId())
                    .estado(pedido.getEstado())
                    .build();
        }

        return null;
    }



    private void descontarCantidadProducto(Producto producto, Integer cantidad) {
        int nuevaCantidad = Integer.valueOf(producto.getCantidad()) - cantidad;
        producto.setCantidad(String.valueOf(nuevaCantidad));
        productoRepository.save(producto);

    }

    @Override
    public String crearItemsPedido(PedidoItemsRequest pedidoItemsRequest) {

        Pedido pedido = pedidoRepository.findById(pedidoItemsRequest.getPedidoId()).orElse(null);

        StringBuilder productosPedido = new StringBuilder();

        for (ProductoSolicitado solicitado : pedidoItemsRequest.getProductosSolicitados()) {
            if (!solicitado.isValido()) {
                continue;
            }

            String idProducto = solicitado.getIdProducto();
            Integer cantidad = solicitado.getCantidadRequerida();
            Producto producto = productoRepository.findById(idProducto).orElse(null);

            if (producto == null) {
                continue;
            }

            ItemPedido itemPedido = ItemPedido.builder()
                    .producto(producto)
                    .pedido(pedido)
                    .cantidad(cantidad)
                    .build();

            itemPedidoRepository.save(itemPedido);
            descontarCantidadProducto(producto, cantidad);

            Integer precioUnidad = producto.getPrecio();
            Integer precioParcial = precioUnidad * cantidad;

            if (productosPedido.length() > 0) {
                productosPedido.append("\n");
            }

            productosPedido.append(producto.getId())
                    .append(",")
                    .append(producto.getNombre())
                    .append(",")
                    .append(precioUnidad)
                    .append(",")
                    .append(cantidad)
                    .append(",")
                    .append(precioParcial).append(";");
        }


        return productosPedido.toString();
    }

    @Override
    public void agendarFechaEnvio(PedidoFechaEnvioRequest pedidoFechaEnvioRequest) {

        Pedido pedido = pedidoRepository.findById(pedidoFechaEnvioRequest.getPedidoId()).orElse(null);

        pedido.setFechaEnvio(pedidoFechaEnvioRequest.getFechaEnvio());

        pedidoRepository.save(pedido);


    }
}
