package com.clientes.config;

import com.clientes.config.UserSessionData; // Opcional, pero bueno para contexto
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class HttpUtil {

    // 🔑 Spring inyecta el Bean 'authenticatedRestTemplate' que creamos en RestClientConfig
    private final RestTemplate restTemplate;

    public HttpUtil(RestTemplate authenticatedRestTemplate) {
        this.restTemplate = authenticatedRestTemplate;
    }

    public <T> T enviarPeticion(
            String url,
            HttpMethod tipo,
            Object body,
            Class<T> tipoDeRetorno) {

        // 1. Configurar Headers (Solo para tipo de contenido, la Autorización la pone el Interceptor)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.ALL));

        // 2. Crear la entidad con el Body y Headers
        HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);

        // 3. Ejecutar la petición. El Interceptor ya añadió el Basic Auth a los Headers
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