package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("/api/hello 返回问候语和版本号")
    void helloReturnsGreeting() throws Exception {
        mockMvc.perform(get("/api/hello").param("name", "CI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, CI!"))
                .andExpect(jsonPath("$.version").exists());
    }

    @Test
    @DisplayName("不传 name 时也能正常返回")
    void helloWithoutName() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, World!"));
    }

    @Test
    @DisplayName("/api/health 返回 UP")
    void healthIsUp() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
