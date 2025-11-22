package com.bpm.engine.pedidos.delegate;

import com.bpm.engine.pedidos.dto.ProductoSolicitado;
import com.bpm.engine.config.HttpUtil;
import com.bpm.engine.pedidos.util.PedidosUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("validarStock")
@Slf4j
@RequiredArgsConstructor
public class ValidarStockDelegate implements JavaDelegate {

    private final HttpUtil httpUtil;
    private final PedidosUtil pedidosUtil;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Iniciando validación de stock");
        try {
            String productos = (String) execution.getVariable("productos");

            if (productos == null || productos.trim().isEmpty()) {
                execution.setVariable("stock", false);
                execution.setVariable("stockFaltante", "No se recibieron productos");
                return;
            }


            List<ProductoSolicitado> productosList = pedidosUtil.parsearProductosSolicitados(productos);

            Map<String, Object> resultado = new HashMap<>();


            resultado = httpUtil.enviarPeticion(
                        "http://localhost:8091/api/pedidos/validar",
                        HttpMethod.POST,
                        productosList,
                        Map.class
            );



            Boolean stockDisponible = (Boolean) resultado.get("stock");
            String stockFaltante = (String) resultado.get("stockFaltante");

            execution.setVariable("stock", stockDisponible);
            execution.setVariable("stockFaltante", stockFaltante);
            execution.setVariable("productosSolicitados", productosList);


        } catch (Exception e) {
            execution.setVariable("stock", false);
            execution.setVariable("stockFaltante", "Error en validación: " + e.getMessage());
        }
    }
}

