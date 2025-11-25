package com.clientes.service.impl;

import com.clientes.config.HttpUtil;
import com.clientes.dto.ProductoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService implements com.clientes.service.ProductoService {

    @Value("${corocora.organizacion.host}")
    private String organizacionBaseUrl;

    @Autowired
    private HttpUtil httpUtil;



    @Override
    public List<ProductoResponse> listarProductos() {

        ParameterizedTypeReference<List<ProductoResponse>> tipoLista =
                new ParameterizedTypeReference<>() {};

        return httpUtil.enviarPeticionOrganizacion(
                organizacionBaseUrl + "api/productos/list",
                HttpMethod.GET,
                null,
                tipoLista
        );
    }
}
