package br.com.projedata;


import br.com.projedata.controller.RawMaterialController;
import br.com.projedata.dto.RawMaterialDTO;
import br.com.projedata.entity.RawMaterial;
import br.com.projedata.excessoes.RawMaterialNotFoundException;
import br.com.projedata.service.RawMaterialService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RawMaterialController.class)
public class RawMaterialControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RawMaterialService rawMaterialService;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should return 201 Created when saving material")
    void shouldReturn201WhenSavingMaterial() throws Exception {

        RawMaterialDTO inputDto = new RawMaterialDTO(null, "Steel", 50.0, new ArrayList<>());
        RawMaterialDTO savedDto = new RawMaterialDTO(1L, "Steel", 50.0, new ArrayList<>());


        when(rawMaterialService.createRawMaterial(any(RawMaterialDTO.class))).thenReturn(savedDto);


        mockMvc.perform(post("/api/material")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Steel"));
    }
    @Test
    @DisplayName("Should return 404 Not Found when material does not exist")
    void getByIdShouldReturnNotFound() throws Exception {
        when(rawMaterialService.getRawMaterialById(99L))
                .thenThrow(new RawMaterialNotFoundException(String.valueOf(99L)));

        mockMvc.perform(get("/api/raw-materials/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 200 OK when finding raw material by ID")
    void getByIdShouldReturnOk() throws Exception {
        Long id = 1L;
        RawMaterialDTO dto = new RawMaterialDTO(id, "Steel", 50.0, new ArrayList<>());
        when(rawMaterialService.getRawMaterialById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/material/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Steel"));
    }

    @Test
    @DisplayName("Should return 200 OK and a list of raw materials")
    void listAllShouldReturnOk() throws Exception {

        RawMaterialDTO material = new RawMaterialDTO(1L, "Wood", 100.0, new ArrayList<>());
        List<RawMaterialDTO> list = List.of(material);
        when(rawMaterialService.listRawMaterials()).thenReturn(list);

        mockMvc.perform(get("/api/material")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Wood"))
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("Should return 200 OK when updating raw material via PATCH")
    void updateShouldReturnOk() throws Exception {

        Long id = 1L;
        RawMaterialDTO inputDto = new RawMaterialDTO(null, "Updated Name", 80.0, new ArrayList<>());
        RawMaterialDTO updatedDto = new RawMaterialDTO(id, "Updated Name", 80.0, new ArrayList<>());


        when(rawMaterialService.updateRawMaterial(eq(id), any(RawMaterialDTO.class)))
                .thenReturn(updatedDto);


        mockMvc.perform(patch("/api/material/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("Should return 204 No Content when deleting raw material")
    void deleteShouldReturnNoContent() throws Exception {
        Long id = 1L;

        doNothing().when(rawMaterialService).deleteRawMaterial(id);

        mockMvc.perform(delete("/api/material/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(rawMaterialService, times(1)).deleteRawMaterial(id);
    }
}
