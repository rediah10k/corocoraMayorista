package com.bpm.engine.pedidos.delegate;

import com.bpm.engine.config.HttpUtil;
import com.bpm.engine.pedidos.dto.PedidoFechaEnvioRequest;
import com.bpm.engine.pedidos.util.PedidosUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service("asignarFechaEnvio")
@Slf4j
@RequiredArgsConstructor
public class AsignarFechaEnvioDelegate implements JavaDelegate {

    private final HttpUtil httpUtil;
    private final PedidosUtil pedidosUtil;

    @Override
    @Transactional
    public void execute(DelegateExecution execution) throws Exception {

        try {

            String fechaHoraEnvio = (String) execution.getVariable("fechaHoraEnvioO");
            Long pedidoId = (Long) execution.getVariable("pedidoIdSub");


            if (pedidoId == null) {
                throw new IllegalArgumentException("pedidoId es requerido");
            }

            if (fechaHoraEnvio == null || fechaHoraEnvio.trim().isEmpty()) {
                throw new IllegalArgumentException("fechaHoraEnvio es requerida");
            }

            LocalDate fechaEnvio = pedidosUtil.parsearFechaString(fechaHoraEnvio);

            httpUtil.enviarPeticion(
                    "http://localhost:8091/api/pedidos/agendar",
                    HttpMethod.PUT,
                    PedidoFechaEnvioRequest.builder()
                            .pedidoId(pedidoId)
                            .fechaEnvio(fechaEnvio)
                            .build(),
                    Void.class
            );


        } catch (Exception e) {
           throw e;
        }
    }



}
