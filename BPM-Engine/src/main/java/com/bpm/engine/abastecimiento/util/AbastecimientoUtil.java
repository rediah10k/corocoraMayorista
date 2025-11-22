package com.bpm.engine.abastecimiento.util;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class AbastecimientoUtil {

    public Map<String, String> parsearCodigoProducto(String codigoProd) {

        if (codigoProd == null || codigoProd.trim().isEmpty()) {
            throw new BpmnError("El código está vacío");
        }

        Map<String, String> datos = new HashMap<>();

        try {
            String[] partes = codigoProd.split(";");

            for (String parte : partes) {
                String[] keyValue = parte.split("=");
                if (keyValue.length == 2) {
                    datos.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
            validarCamposRequeridos(datos);
            return datos;

        } catch (Exception e) {
            throw new BpmnError("Error al parsear: " + e.getMessage());
        }
    }

    private void validarCamposRequeridos(Map<String, String> datos) {
        String[] camposRequeridos = {"LOTE", "CANT", "PROD"};

        for (String campo : camposRequeridos) {
            if (!datos.containsKey(campo) || datos.get(campo).isEmpty()) {
                throw new BpmnError("Falta el campo requerido: " + campo);
            }
        }
    }
}
