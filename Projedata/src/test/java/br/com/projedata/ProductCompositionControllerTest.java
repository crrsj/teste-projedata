package br.com.projedata;


import br.com.projedata.controller.ProductCompositionController;
import br.com.projedata.dto.ProductCompositionDTO;

import br.com.projedata.dto.ProductionSuggestionDTO;

import br.com.projedata.service.ProductCompositionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;

import static org.mockito.Mockito.*;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;






@WebMvcTest(ProductCompositionController.class)
public class ProductCompositionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductCompositionService compositionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProductCompositionDTO compositionDTO;
    private final String productName = "Mesa";
    private final Long productId = 1L;
    private final Long rawMaterialId = 2L;

    @BeforeEach
    void setUp() {

        compositionDTO = new ProductCompositionDTO(productId, rawMaterialId, productName, 10.0);
    }
    @Test
    @DisplayName("Should save composition and return 201 Created")
    void shouldSaveCompositionSuccessfully() throws Exception {

        ProductCompositionDTO inputDto = new ProductCompositionDTO(productId, rawMaterialId, productName, 10.0);
        when(compositionService.saveComposition(eq(productId), eq(rawMaterialId), any(ProductCompositionDTO.class)))
                .thenReturn(inputDto);
        mockMvc.perform(post("/api/composition/{productId}/{rawMaterialId}", productId, rawMaterialId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.rawMaterialId").value(rawMaterialId))
                .andExpect(jsonPath("$.productName").value(productName)) // Valida o nome recolocado
                .andExpect(jsonPath("$.quantity").value(10.0));
    }

    @Test
    @DisplayName("Should return 200 OK and list of production suggestions")
    void shouldReturnProductionSuggestionsSuccessfully() throws Exception {
        ProductionSuggestionDTO suggestion1 = new ProductionSuggestionDTO("Mesa", 5, BigDecimal.valueOf(1500.0));
        ProductionSuggestionDTO suggestion2 = new ProductionSuggestionDTO("Cadeira", 12, BigDecimal.valueOf(2400.0));
        List<ProductionSuggestionDTO> mockResponse = List.of(suggestion1, suggestion2);
        when(compositionService.calculateSuggestion()).thenReturn(mockResponse);
        mockMvc.perform(get("/api/composition/calculate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productName").value("Mesa"))
                .andExpect(jsonPath("$[0].quantity").value(5))
                .andExpect(jsonPath("$[0].totalValue").value(1500.0))
                .andExpect(jsonPath("$[1].productName").value("Cadeira"))
                .andExpect(jsonPath("$[1].quantity").value(12));
        verify(compositionService, times(1)).calculateSuggestion();
    }

    @Test
    @DisplayName("Should return 404 when Product or Raw Material is not found")
    void shouldReturn404WhenIdsDoNotExist() throws Exception {
        when(compositionService.saveComposition(anyLong(), anyLong(), any(ProductCompositionDTO.class)))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
        mockMvc.perform(post("/api/composition/{productId}/{rawMaterialId}", 99L, 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compositionDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 200 OK and empty list when no suggestions are available")
    void shouldReturnEmptyListWhenNoSuggestions() throws Exception {
        when(compositionService.calculateSuggestion()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/composition/calculate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
