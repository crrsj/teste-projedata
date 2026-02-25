package br.com.projedata;


import br.com.projedata.controller.ProductController;
import br.com.projedata.dto.ProductDTO;
import br.com.projedata.excessoes.ProductNotFoundException;
import br.com.projedata.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;
    private Long id = 1L;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO(id, "Product Test", BigDecimal.valueOf(100.0), new ArrayList<>());
    }

    @Test
    @DisplayName("Should create product and return 201 Created")
    void shouldCreateProductSuccessfully() throws Exception {
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Product Test"))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    @Test
    @DisplayName("Should return 200 OK when finding product by ID")
    void shouldFindProductById() throws Exception {
        when(productService.getProductById(id)).thenReturn(productDTO);

        mockMvc.perform(get("/api/product/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Product Test"));
    }

    @Test
    @DisplayName("Should return 404 Not Found when product does not exist")
    void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        when(productService.getProductById(id)).thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(get("/product/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 200 OK when listing all products")
    void shouldListAllProducts() throws Exception {
        when(productService.ListProducts()).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("Should return 200 OK when updating product successfully")
    void shouldUpdateProductSuccessfully() throws Exception {
        // Arrange
        Long id = 1L;
        ProductDTO updateRequest = new ProductDTO(null, "Updated Name", BigDecimal.valueOf(250.0), new ArrayList<>());
        ProductDTO updatedResponse = new ProductDTO(id, "Updated Name", BigDecimal.valueOf(250.0), new ArrayList<>());

        when(productService.updateProducts(eq(id), any(ProductDTO.class))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/product/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.price").value(250.0));

        verify(productService, times(1)).updateProducts(eq(id), any(ProductDTO.class));
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating non-existent product")
    void shouldReturnNotFoundWhenUpdatingNonExistentProduct() throws Exception {

        Long id = 99L;
        ProductDTO updateRequest = new ProductDTO(null, "New Name", BigDecimal.valueOf(100.0), new ArrayList<>());

        when(productService.updateProducts(eq(id), any(ProductDTO.class)))
                .thenThrow(new ProductNotFoundException("Product not found"));


        mockMvc.perform(patch("/api/product/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 204 No Content when deleting product")
    void shouldDeleteProductSuccessfully() throws Exception {
        doNothing().when(productService).deleteProducts(id);
        mockMvc.perform(delete("/api/product/{id}", id))
                .andExpect(status().isNoContent());
    }
}
