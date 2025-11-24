package com.bpm.engine.config;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;

@Configuration
public class CamundaConfig {

    @Bean
    public CommandLineRunner initUsers(IdentityService identityService) {
        return args -> {
            // Crear usuario demo si no existe
            if (identityService.createUserQuery().userId("demo").count() == 0) {
                User user = identityService.newUser("demo");
                user.setFirstName("Demo");
                user.setLastName("User");
                user.setPassword("demo");
                user.setEmail("demo@example.com");
                identityService.saveUser(user);
            }

            // Crear grupo camunda-admin si no existe
            if (identityService.createGroupQuery().groupId("camunda-admin").count() == 0) {
                Group adminGroup = identityService.newGroup("camunda-admin");
                adminGroup.setName("camunda BPM Administrators");
                adminGroup.setType("SYSTEM");
                identityService.saveGroup(adminGroup);
            }

            // Añadir usuario demo al grupo camunda-admin
            if (identityService.createGroupQuery()
                    .groupMember("demo")
                    .groupId("camunda-admin")
                    .count() == 0) {
                identityService.createMembership("demo", "camunda-admin");
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Interceptor que añade el encabezado de Basic Auth antes de la ejecución
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            String auth = "camunda@gmail.com" + ":" + "camunda";
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
            // Añade el encabezado de autorización
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, authHeader);

            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(Collections.singletonList(interceptor));

        return restTemplate;
    }





}

