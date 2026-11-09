package com.example.demo;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final GreetingService greetingService;

    public HelloController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/hello")
    public Map<String, Object> hello(@RequestParam(required = false) String name) {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("message", greetingService.greet(name));
        body.put("version", greetingService.getVersion());
        return body;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("status", "UP");
        return body;
    }
}
