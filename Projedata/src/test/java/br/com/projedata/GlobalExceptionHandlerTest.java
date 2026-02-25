package br.com.projedata;

import br.com.projedata.exceptions.GlobalExceptionHandler;
import br.com.projedata.exceptions.ProductNotFoundException;
import br.com.projedata.exceptions.RawMaterialNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(new FakeController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should handle ProductNotFoundException and return 404")
    void shouldHandleProductNotFoundException() throws Exception {
        mockMvc.perform(get("/test/product-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Product not found"))
                .andExpect(jsonPath("$.path").value("/test/product-not-found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should handle RawMaterialNotFoundException and return 404")
    void shouldHandleRawMaterialNotFoundException() throws Exception {
        mockMvc.perform(get("/test/material-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Material not found"))
                .andExpect(jsonPath("$.path").value("/test/material-not-found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }


    @RestController
    private static class FakeController {

        @GetMapping("/test/product-not-found")
        public void throwProduct() {
            throw new ProductNotFoundException("Product not found");
        }

        @GetMapping("/test/material-not-found")
        public void throwMaterial() {
            throw new RawMaterialNotFoundException("Material not found");
        }
    }
}