package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GreetingServiceTest {

    private final GreetingService service = new GreetingService("1.0.0");

    @Test
    @DisplayName("传名字时，返回 Hello, 名字!")
    void greetsByName() {
        assertEquals("Hello, CI!", service.greet("CI"));
    }

    @Test
    @DisplayName("名字为空或空白时，回退到 World")
    void fallsBackToWorld() {
        assertEquals("Hello, World!", service.greet(null));
        assertEquals("Hello, World!", service.greet("   "));
    }

    @Test
    @DisplayName("前后空格要被 trim 掉")
    void trimsWhitespace() {
        assertEquals("Hello, Bob!", service.greet("  Bob  "));
    }

    @Test
    @DisplayName("构造时注入的版本号要能读出来")
    void exposesVersion() {
        assertEquals("1.0.0", service.getVersion());
    }
}
