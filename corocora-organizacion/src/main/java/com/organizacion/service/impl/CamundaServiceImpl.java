package com.organizacion.service.impl;

import com.organizacion.config.HttpUtil;
import com.organizacion.dto.CamundaActivityInstance;
import com.organizacion.dto.CamundaTask;
import com.organizacion.dto.TareaActivaInfo;
import com.organizacion.service.CamundaService;
import com.organizacion.service.ClienteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CamundaServiceImpl implements CamundaService {


    @Value("${camunda.url}")
    private String camundaBaseUrl;

    private HttpUtil httpUtil;

    public CamundaServiceImpl(HttpUtil httpUtil) {
        this.httpUtil = httpUtil;
    }

    public List<TareaActivaInfo> obtenerIdsActivosEnActivityId(String activityId) {

        // URL para obtener instancias de actividad que NO han terminado
        String url = camundaBaseUrl + "history/activity-instance?sortBy=startTime&sortOrder=desc&activityId=" + activityId + "&finished=false&unfinished=true&withoutTenantId=false";

        // Definición del tipo de retorno
        ParameterizedTypeReference<List<CamundaActivityInstance>> tipoRetorno =
                new ParameterizedTypeReference<List<CamundaActivityInstance>>() {};

        // 1. Llamada a la API de Camunda
        List<CamundaActivityInstance> instanciasActivas = httpUtil.enviarPeticionCamunda(
                url,
                HttpMethod.GET,
                null,
                tipoRetorno
        );

        // 2. Mapear a la DTO que contiene ambos IDs
        List<TareaActivaInfo> infoTareasActivas = instanciasActivas.stream()
                // Es buena práctica filtrar nulos que podrían no ser UserTasks completables
                .filter(inst -> inst.getTaskId() != null && inst.getRootProcessInstanceId() != null)
                .map(inst -> new TareaActivaInfo(
                        inst.getRootProcessInstanceId(), // El ID Raíz
                        inst.getTaskId()                 // El ID para completar la tarea
                ))
                .collect(Collectors.toList());

        return infoTareasActivas;
    }



    public Map<String, Object> obtenerVariablesPorIdUnico(String processId) {

        // 1. Configura la URL para obtener las variables históricas, ordenadas por más reciente primero.
        String url = camundaBaseUrl + "history/variable-instance?processInstanceId=" + processId;
                 // Esto asegura que la última versión sea la v1 en el stream

        // ... (Código para la llamada HTTP omitido) ...
        List<Map<String, Object>> listaVariablesMapa = httpUtil.enviarPeticionCamunda(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );


// ... (asume que listaVariablesMapa es List<Map<String, Object>>)

        Map<String, Object> variablesFinales = listaVariablesMapa.stream()
                // 💡 FILTRO AÑADIDO: Excluye cualquier mapa donde el campo "value" sea null.
                .filter(m -> m.get("value") != null)
                .collect(Collectors.toMap(
                        m -> (String) m.get("name"),   // Clave del mapa: 'name'
                        m -> m.get("value"),           // Valor del mapa: 'value'
                        (v1, v2) -> v1,                // Manejo de duplicados (mantiene el más reciente/primero)
                        HashMap::new
                ));

        return variablesFinales;
    }

    public void completarTareaEspecifica(String taskId, Map<String, Object> taskVariables) {

        // 1. Construir la URL de completado directamente con el taskId proporcionado.
        String completeUrl = camundaBaseUrl + "task/" + taskId + "/complete";

        // 2. Formatear las variables para Camunda
        Map<String, Object> variablesFormateadas = new HashMap<>();

        for (Map.Entry<String, Object> entry : taskVariables.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 💡 Asumimos que todas las variables son de tipo String para Camunda
            variablesFormateadas.put(key, Map.of("value", value, "type", "String"));
        }

        // 3. Crear el cuerpo de la petición
        Map<String, Object> requestBody = Map.of("variables", variablesFormateadas);

        // 4. Ejecutar la petición POST para completar la tarea
        httpUtil.enviarPeticionCamunda(
                completeUrl,
                HttpMethod.POST,
                requestBody,
                new ParameterizedTypeReference<Void>() {}
        );
    }


}
