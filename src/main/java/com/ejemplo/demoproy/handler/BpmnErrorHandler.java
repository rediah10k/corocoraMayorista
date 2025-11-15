package com.ejemplo.demoproy.handler;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Manejador de errores para eventos de error en BPMN.
 * Este delegate puede ser usado en boundary events o error end events.
 */
@Component("errorHandler")
@Slf4j
public class BpmnErrorHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String errorCode = (String) execution.getVariable("errorCode");
        String errorMessage = (String) execution.getVariable("errorMessage");

        log.error("Error capturado en proceso BPMN - Código: {}, Mensaje: {}", errorCode, errorMessage);

        // Guardar información del error para auditoría
        execution.setVariable("errorProcessed", true);
        execution.setVariable("errorTimestamp", java.time.LocalDateTime.now().toString());
    }
}

