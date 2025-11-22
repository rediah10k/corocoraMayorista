package com.bpm.engine.pedidos.delegate;

import com.bpm.engine.config.HttpUtil;
import com.bpm.engine.pedidos.dto.ClienteResponse;
import com.bpm.engine.pedidos.dto.ProductoSolicitado;
import com.bpm.engine.pedidos.dto.PedidoItemsRequest;
import com.bpm.engine.pedidos.dto.PedidoRequest;
import com.bpm.engine.pedidos.dto.PedidoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("generarMovimiento")
@Slf4j
@RequiredArgsConstructor
public class GenerarMovimientoDelegate implements JavaDelegate {
    private final HttpUtil httpUtil;


    @Override
    @Transactional
    public void execute(DelegateExecution execution) throws Exception {


        try {
            String numeroDocumentoString = (String) execution.getVariable("numeroDocumento");
            List<ProductoSolicitado> productoSolicitados = (List<ProductoSolicitado>) execution.getVariable("productosSolicitados");
            Boolean stock = (Boolean) execution.getVariable("stock");
            Long numeroDocumento = Long.parseLong(numeroDocumentoString);

            ClienteResponse clienteResponse = httpUtil.enviarPeticion(
                    "http://localhost:8091/api/clientes/" + numeroDocumento,
                    HttpMethod.GET,
                    null,
                    ClienteResponse.class
                    );


            if (clienteResponse == null) {
                execution.setVariable("stock", false);
                execution.setVariable("stockFaltante", "La persona con documento " + numeroDocumento + " no existe");
                return;
            }

            String estadoPedido = Boolean.TRUE.equals(stock) ? "RESERVADO" : "PENDIENTE";


            PedidoResponse pedidoResponse = httpUtil.enviarPeticion(
                    "http://localhost:8091/api/pedidos/crear",
                    HttpMethod.POST,
                    PedidoRequest.builder()
                            .clienteId(clienteResponse.getId())
                            .estado(estadoPedido)
                            .build(),
                    PedidoResponse.class
            );


            String productosPedido = new String();

            if (Boolean.TRUE.equals(stock) && productoSolicitados != null && !productoSolicitados.isEmpty()) {

                productosPedido = httpUtil.enviarPeticion(
                        "http://localhost:8091/api/pedidos/items",
                        HttpMethod.POST,
                        PedidoItemsRequest.builder()
                                .pedidoId(pedidoResponse.getPedidoId())
                                .productosSolicitados(productoSolicitados)
                                .build(),
                        String.class
                );
            }

            execution.setVariable("pedidoId", pedidoResponse.getPedidoId());
            execution.setVariable("clienteNombre", clienteResponse.getNombres());
            execution.setVariable("productosPedido", productosPedido);

        } catch (Exception e) {
            execution.setVariable("stock", false);
            execution.setVariable("stockFaltante", "Error al generar movimiento: " + e.getMessage());
            throw e;
        }
    }


}