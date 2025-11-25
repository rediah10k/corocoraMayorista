package com.clientes.service.impl;


import com.clientes.config.HttpUtil;
import com.clientes.config.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CamundaPedidosService implements com.clientes.service.CamundaPedidosService {

    @Value("${camunda.url}")
    private String camundaBaseUrl;

    @Value("${camunda.pedidos.process.name}")
    private String processName;

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private UserSessionData sessionData;


    @Override
    public void iniciarNuevoPedido(String productos) {

        String url = camundaBaseUrl + "process-definition/key/" + processName + "/start";

        // 1. Crear las variables del proceso usando Map
        Map<String, Object> variables = new HashMap<>();

        // Definición de variables internas (Valor y Tipo)

        // Variable 1: ID del Cliente (Tipo Long)
        Map<String, Object> documentoVar = new HashMap<>();
        documentoVar.put("value", sessionData.getClienteInfo().getDocumento());
        documentoVar.put("type", "Long");
        variables.put("documento", documentoVar);

        // Variable 2: Items del Pedido (Tipo String)
        Map<String, Object> productosVar = new HashMap<>();
        productosVar.put("value", productos);
        productosVar.put("type", "String");
        variables.put("productos", productosVar);

        // Puedes agregar más variables aquí si las necesitas...

        // 2. Crear el Body COMPLETO de la petición (StartProcessRequest simulado)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("variables", variables);

        ParameterizedTypeReference<Void> tipoVoid = new ParameterizedTypeReference<Void>() {};

        httpUtil.enviarPeticionCamunda(
                url,
                HttpMethod.POST,
                requestBody,
                tipoVoid
        );

    }
}
