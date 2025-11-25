package com.organizacion.controller;


import com.organizacion.config.UserSessionData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class LoginController {


    private final UserSessionData sessionData;


    @GetMapping("/login")
    public String showLoginForm() {

        return "login"; // Asume que la plantilla se llama login.html

    }





}
