package com.bpm.engine.abastecimiento.delegate;

import com.bpm.engine.abastecimiento.dto.RecepcionRequest;
import com.bpm.engine.abastecimiento.util.AbastecimientoUtil;
import com.bpm.engine.config.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service("cargarProducto")
@Slf4j
@RequiredArgsConstructor
public class InsertarInventarioDelegate implements JavaDelegate {

    private final HttpUtil httpUtil;
    private final AbastecimientoUtil abastecimientoUtil;


    @Override
    public void execute(DelegateExecution execution) throws Exception {

            String codigoProd = (String) execution.getVariable("codigoProd");
            String fechaVencimiento = (String) execution.getVariable("fechaVencimiento");
            log.info("Código recibido: {}", codigoProd);

            Map<String, String> datosParseados = abastecimientoUtil.parsearCodigoProducto(codigoProd);

            Long recepcionId = httpUtil.enviarPeticion(
                    "http://localhost:8091/api/inventario/insertar",
                    HttpMethod.POST,
                    RecepcionRequest.builder()
                            .lote(datosParseados.get("LOTE"))
                            .producto(datosParseados.get("PROD"))
                            .cantidad(Integer.parseInt(datosParseados.get("CANT")))
                            .fechaVencimiento(fechaVencimiento)
                            .build(),
                    Long.class
            );


            execution.setVariable("recepcionId", recepcionId);

            log.info("Inventario creado exitosamente con ID: {}", recepcionId);

    }
}