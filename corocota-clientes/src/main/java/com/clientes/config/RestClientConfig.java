package com.clientes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;

@Configuration
public class RestClientConfig {

    // 🔑 Bean de RestTemplate con el interceptor de Basic Auth
    @Bean
    public RestTemplate authenticatedRestTemplate(UserSessionData sessionData) {
        RestTemplate restTemplate = new RestTemplate();

        // Interceptor que añade el encabezado de Basic Auth antes de la ejecución
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            if (sessionData.isAuthenticated()) {
                String auth = sessionData.getUsername() + ":" + sessionData.getPassword();
                String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
                // Añade el encabezado de autorización
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, authHeader);
            }
            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(Collections.singletonList(interceptor));

        return restTemplate;
    }
}