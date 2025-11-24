package com.bpm.engine;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoProyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoProyApplication.class, args);
    }

}
