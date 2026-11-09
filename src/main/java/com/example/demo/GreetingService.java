package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    private final String version;

    public GreetingService(@Value("${app.version:dev}") String version) {
        this.version = version;
    }

    public String greet(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Hello, World!";
        }
        return "Hello, " + name.trim() + "!";
    }

    public String getVersion() {
        return version;
    }
}
