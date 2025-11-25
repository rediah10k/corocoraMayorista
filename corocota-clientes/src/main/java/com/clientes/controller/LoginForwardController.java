package com.clientes.controller;

import com.clientes.config.ClienteResponse;
import com.clientes.config.UserSessionData;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Controller // Usamos Controller para manejar vistas y redirecciones.
public class LoginForwardController {

    private final WebClient webClient;
    private final UserSessionData sessionData;

    private static final String INFO_URL = "http://localhost:8091/api/clientes/info";

    public LoginForwardController(WebClient.Builder webClientBuilder, UserSessionData sessionData) {
        this.webClient = webClientBuilder.build();
        this.sessionData = sessionData;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Asume que la plantilla se llama login.html
    }


    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password) {

        String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        try {
            // 1. Llama a la URL protegida y obtiene el objeto ClienteResponse
            ClienteResponse clienteInfo = webClient.get()
                    .uri(INFO_URL)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .bodyToMono(ClienteResponse.class)
                    .block();

            // 2. Si la llamada es exitosa y hay datos
            if (clienteInfo != null && clienteInfo.getId() != null) {

                // 3. Almacenar credenciales y autenticación en la sesión
                sessionData.setUsername(username);
                sessionData.setPassword(password);
                sessionData.setAuthenticated(true);

                // 🔑 CLAVE: Almacenar los datos del cliente en el bean de sesión
                sessionData.setClienteInfo(clienteInfo);

                // 4. Redirigir al endpoint de la vista
                return "redirect:/";
            }

            return "redirect:/login?error=auth_fail";

        } catch (Exception e) {
            // Maneja 401 Unauthorized o fallos de conexión
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/")
    public String goHome(Model model) { // 🔑 CLAVE: Inyectar Model

        // 1. Verificar autenticación
        if (!sessionData.isAuthenticated() || sessionData.getClienteInfo() == null) {
           return "redirect:/login";
        }

        // 2. 🔑 CLAVE: Añadir el objeto ClienteResponse del bean de sesión al Model
        // Este objeto ahora estará disponible en 'inicio.html' con el nombre 'clienteInfo'
        model.addAttribute("clienteInfo", sessionData.getClienteInfo());

        return "index"; // Renderiza la plantilla 'inicio.html'
    }
}