package com.ejemplo.demoproy.delegate;

import com.ejemplo.demoproy.service.InventarioService;
import com.ejemplo.demoproy.exception.CodigoInvalidoException;
import com.ejemplo.demoproy.exception.ProductoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("cargarProducto")
@Slf4j
@RequiredArgsConstructor
public class InsertarInventarioDelegate implements JavaDelegate {

    private final InventarioService inventarioService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        try {

            String codigoProd = (String) execution.getVariable("codigoProd");
            String fechaVencimiento = (String) execution.getVariable("fechaVencimiento");
            log.info("Código recibido: {}", codigoProd);

            Map<String, String> datosParsed = inventarioService.parsearCodigoProducto(codigoProd);
            Long inventarioId = inventarioService.insertarInventarioInicial(datosParsed, fechaVencimiento);
            execution.setVariable("inventarioId", inventarioId);

            log.info("Inventario creado exitosamente con ID: {}", inventarioId);

        } catch (CodigoInvalidoException e) {
            log.error("Error de validación: {}", e.getMessage());
            throw new BpmnError("CODIGO_INVALIDO", e.getMessage());

        } catch (ProductoNoEncontradoException e) {
            log.error("Producto no encontrado: {}", e.getMessage());
            throw new BpmnError("PRODUCTO_NO_ENCONTRADO", e.getMessage());

        } catch (Exception e) {
            log.error("Error inesperado en ParsearInventarioDelegate", e);
            throw new BpmnError("ERROR_TECNICO", "Error al procesar inventario: " + e.getMessage());
        }
    }
}