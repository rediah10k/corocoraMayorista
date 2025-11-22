package com.bpm.engine.config;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class HttpUtil {

    private final RestTemplate restTemplate;

    public HttpUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T enviarPeticion(
            String url,
            HttpMethod tipo,
            Object body,
            Class<T> tipoDeRetorno) {

        // 1. Configurar Headers para JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.ALL));

        // 2. Crear la entidad con el Body (puede ser null) y Headers
        HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);

        // 3. Ejecutar la petición (la URL ya debe incluir cualquier parámetro)
        ResponseEntity<T> response = restTemplate.exchange(
                url,
                tipo,
                requestEntity,
                tipoDeRetorno
        );

        // 4. Retornar el body
        return response.getBody();
    }
}
