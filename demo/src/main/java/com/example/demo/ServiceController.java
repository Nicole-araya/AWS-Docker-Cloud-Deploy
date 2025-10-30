package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceController {

    @GetMapping("/health")
    public String healthCheck() { 
        return "{\"status\": \"UP\"}";
    }
    @GetMapping("/data")
    public String getData() { 
        String hostname = System.getenv("HOSTNAME") != null ? System.getenv("HOSTNAME") : "Local Machine";
        return "Microservicio corriendo en el Host: " + hostname;
    }
}

