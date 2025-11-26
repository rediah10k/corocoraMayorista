package com.clientes.service.impl;


import com.clientes.config.HttpUtil;
import com.clientes.config.UserSessionData;
import com.clientes.service.PedidoService;
import com.clientes.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CamundaServiceImpl implements com.clientes.service.CamundaService {

    @Value("${camunda.url}")
    private String camundaBaseUrl;

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private PedidoService pedidoService;


    @Override
    public String iniciarNuevoProceso(String processKey, Map<String, Object> variables) {

        String url = camundaBaseUrl + "process-definition/key/" + processKey + "/start";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("variables", variables);
        requestBody.put("withVariablesInReturn", true); // Aunque no estrictamente necesario para el ID, es buena práctica.

        ParameterizedTypeReference<Map<String, Object>> tipoRetorno =
                new ParameterizedTypeReference<>() {};


        Map<String, Object> respuesta = httpUtil.enviarPeticionCamunda(
                url,
                HttpMethod.POST,
                requestBody,
                tipoRetorno
        );

        String processIdGenerado = (String) respuesta.get("id");

        try {
            pedidoService.guardarProcessIdPedido(processIdGenerado);
        } catch (Exception e) {
            System.err.println("ERROR AL GUARDAR PROCESO");
            e.printStackTrace();
        }
        return processIdGenerado;
    }
}
