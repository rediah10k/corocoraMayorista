package com.organizacion.service;

import com.organizacion.dto.TareaActivaInfo;

import java.util.List;
import java.util.Map;

public interface CamundaService {
    List<TareaActivaInfo> obtenerIdsActivosEnActivityId(String activityId);

    Map<String, Object> obtenerVariablesPorIdUnico(String processId);

    void completarTareaEspecifica(String taskId, Map<String, Object> taskVariables);

}
