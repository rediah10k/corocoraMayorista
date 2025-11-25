package com.clientes.config;

import com.clientes.config.UserSessionData; // Opcional, pero bueno para contexto
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;

@Component
public class HttpUtil {

    UserSessionData sessionData;

    public HttpUtil(UserSessionData sessionData){
        this.sessionData = sessionData;
    }

    public <T> T enviarPeticionOrganizacion(
            String url,
            HttpMethod tipo,
            Object body,
            ParameterizedTypeReference<T> tipoDeRetorno) {

        RestTemplate restTemplate = new RestTemplate();

        // Interceptor que añade el encabezado de Basic Auth antes de la ejecución
        ClientHttpRequestInterceptor interceptor = (request, body_, execution) -> {
            if (sessionData.isAuthenticated()) {
                String auth = sessionData.getUsername() + ":" + sessionData.getPassword();
                String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
                // Añade el encabezado de autorización
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, authHeader);
            }
            return execution.execute(request, body_);
        };

        restTemplate.setInterceptors(Collections.singletonList(interceptor));


        // Simplemente llama a la lógica de ejecución con el REST TEMPLATE AUTENTICADO
        return ejecutarPeticion(restTemplate, url, tipo, body, tipoDeRetorno);
    }

    // 🔑 Nuevo método público: Usa el RestTemplate SIN Autenticación
    public <T> T enviarPeticionCamunda(
            String url,
            HttpMethod tipo,
            Object body,
            ParameterizedTypeReference<T> tipoDeRetorno) {


            RestTemplate restTemplate = new RestTemplate();


        // Simplemente llama a la lógica de ejecución con el REST TEMPLATE BÁSICO
        return ejecutarPeticion(restTemplate, url, tipo, body, tipoDeRetorno);
    }

    // 🧱 Lógica Común: Aquí está tu código original de construir Headers y hacer el exchange.
// Este método es privado y recibe el RestTemplate a usar.
    private <T> T ejecutarPeticion(
            RestTemplate restTemplate, // ⬅️ Recibe el template a usar
            String url,
            HttpMethod tipo,
            Object body,
            ParameterizedTypeReference<T> tipoDeRetorno) {

        // 1. Configurar Headers (siempre lo mismo)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.ALL));

        // 2. Crear la entidad
        HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);

        // 3. Ejecutar la petición con el RestTemplate que fue pasado.
        ResponseEntity<T> response = restTemplate.exchange(
                url,
                tipo,
                requestEntity,
                tipoDeRetorno
        );
        return response.getBody();
    }
}