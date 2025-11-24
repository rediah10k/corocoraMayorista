package com.organizacion.config;


import com.organizacion.repository.ClienteRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final ClienteRepository clienteRepository;

    // --- 1. Filtro de Seguridad (incluye CSRF y CORS) ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // ... (otras rutas) ...
                        .requestMatchers("/api/clientes/crear").permitAll()
                        .requestMatchers("/api/**").authenticated() // Protege tus APIs
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults()) // 🔑 Habilita Basic Authentication para API
                .csrf(csrf -> csrf.disable()); // Deshabilita CSRF, ya que es Stateless/API

        // ... (Mantener UserDetailsService y PasswordEncoder) ...

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> clienteRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // NOTA: Recuerda que también necesitas el PasswordEncoder y el UserDetailsService
    // (o el soporte JDBC) en esta clase para que el login funcione.
}