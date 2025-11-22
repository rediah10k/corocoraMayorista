package com.bpm.engine.config;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

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
        return new RestTemplate();
    }



}

