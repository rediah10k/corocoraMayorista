package com.clientes.service;

import java.util.Map;

public interface CamundaService {

    String iniciarNuevoProceso(String processKey, Map<String, Object> variables);

}
