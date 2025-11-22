package com.organizacion.service;

import com.organizacion.dto.*;
import com.organizacion.enitity.Producto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PedidosService {
    Map<String, Object> validarStockProductos(List<ProductoSolicitado> productosSolicitados);

    PedidoResponse crearPedido(PedidoRequest pedidoRequest);

    String crearItemsPedido(PedidoItemsRequest pedidoItemsRequest);

    void agendarFechaEnvio(PedidoFechaEnvioRequest pedidoFechaEnvioRequest);

}
