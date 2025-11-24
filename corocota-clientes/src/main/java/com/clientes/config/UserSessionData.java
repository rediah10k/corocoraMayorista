package com.clientes.config;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope // 🔑 Almacenado en la memoria de la sesión del usuario (en RAM)
@Data
public class UserSessionData {
    private String username;
    private String password;
    private boolean isAuthenticated = false;
    private ClienteResponse clienteInfo;
}