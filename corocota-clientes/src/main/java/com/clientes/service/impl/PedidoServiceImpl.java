package com.clientes.service.impl;

import com.clientes.config.HttpUtil;
import com.clientes.dto.ProductoResponse;
import com.clientes.service.PedidoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PedidoServiceImpl implements PedidoService {


    @Value("${corocora.organizacion.host}")
    private String organizacionBaseUrl;

    @Autowired
    private HttpUtil httpUtil;


    @Override
    public void guardarProcessIdPedido(String processId) {
        ParameterizedTypeReference<Void> tipoRetorno =
                new ParameterizedTypeReference<>() {};

        httpUtil.enviarPeticionOrganizacion(
                organizacionBaseUrl + "api/procesos/crear",
                HttpMethod.POST,
                processId,
                tipoRetorno
        );

    }



}
