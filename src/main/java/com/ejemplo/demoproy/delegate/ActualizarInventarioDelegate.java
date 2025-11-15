package com.ejemplo.demoproy.delegate;

import com.ejemplo.demoproy.exception.CodigoInvalidoException;
import com.ejemplo.demoproy.exception.InventarioNoEncontradoException;
import com.ejemplo.demoproy.service.InventarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service("guardarInfoAlmacen")
@Slf4j
@RequiredArgsConstructor
public class ActualizarInventarioDelegate implements JavaDelegate {

    private final InventarioService inventarioService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {


        try {

            Long inventarioId = (Long) execution.getVariable("inventarioId");
            String fechaIngreso = (String) execution.getVariable("fechaIngreso");
            Integer prioridadSalida = (Integer) execution.getVariable("prioridadSalida");
            String tipoRotacion = (String) execution.getVariable("tipoRotacion");
            String ubicacionAlmacen = (String) execution.getVariable("ubicacionAlmacen");

            inventarioService.actualizarInventario(
                    inventarioId,
                    fechaIngreso,
                    prioridadSalida,
                    tipoRotacion,
                    ubicacionAlmacen
            );

        } catch (CodigoInvalidoException e) {
            log.error("Error de validación: {}", e.getMessage());
            throw new BpmnError("ERROR_VALIDACION", e.getMessage());

        } catch (InventarioNoEncontradoException e) {
            log.error("Inventario no encontrado: {}", e.getMessage());
            throw new BpmnError("INVENTARIO_NO_ENCONTRADO", e.getMessage());

        } catch (DataAccessException e) {
            log.error("Error de acceso a base de datos al actualizar inventario", e);
            throw new BpmnError("ERROR_BASE_DATOS", "Error de base de datos: " + e.getMessage());

        } catch (Exception e) {
            log.error("Error al actualizar inventario", e);
            throw new BpmnError("ERROR_ACTUALIZACION", "Error al actualizar inventario: " + e.getMessage());
        }
    }
}