package com.organizacion.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Clase para mapear la respuesta de /history/activity-instance
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CamundaActivityInstance {

    // CRUCIAL: Debe coincidir con el campo JSON de Camunda
    private String rootProcessInstanceId;
    private String taskId;

}