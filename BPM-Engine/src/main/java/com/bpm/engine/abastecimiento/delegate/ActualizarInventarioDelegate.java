package com.bpm.engine.abastecimiento.delegate;

import com.bpm.engine.abastecimiento.dto.RecepcionActualizacionRequest;
import com.bpm.engine.abastecimiento.dto.RecepcionRequest;
import com.bpm.engine.config.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service("guardarInfoAlmacen")
@Slf4j
@RequiredArgsConstructor
public class ActualizarInventarioDelegate implements JavaDelegate {
    private final HttpUtil httpUtil;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

            Long recepcionId = (Long) execution.getVariable("recepcionId");
            String fechaIngreso = (String) execution.getVariable("fechaIngreso");
            Integer prioridadSalida = (Integer) execution.getVariable("prioridadSalida");
            String tipoRotacion = (String) execution.getVariable("tipoRotacion");
            String ubicacionAlmacen = (String) execution.getVariable("ubicacionAlmacen");


            httpUtil.enviarPeticion(
                    "http://localhost:8091/api/inventario/actualizar",
                    HttpMethod.PUT,
                    RecepcionActualizacionRequest.builder()
                            .recepcionId(recepcionId)
                            .fechaIngreso(fechaIngreso)
                            .prioridadSalida(prioridadSalida)
                            .tipoRotacion(tipoRotacion)
                            .ubicacionAlmacen(ubicacionAlmacen)
                            .build(),
                    Void.class
            );

    }
}