package com.organizacion.config;

import com.organizacion.dto.ClienteResponse;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;


@Component
@SessionScope // 🔑 Almacenado en la memoria de la sesión del usuario (en RAM)
@Data
public class UserSessionData {
    private String usernameAdmin;
    private String passwordAdmin;
    private boolean isAuthenticatedAdmin = false;
    private ClienteResponse clienteInfoAdmin;
}