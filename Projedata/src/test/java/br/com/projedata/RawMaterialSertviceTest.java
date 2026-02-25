package br.com.projedata;

import br.com.projedata.dto.RawMaterialDTO;
import br.com.projedata.entity.RawMaterial;
import br.com.projedata.exceptions.RawMaterialNotFoundException;
import br.com.projedata.repository.ProductCompositionRepository;
import br.com.projedata.repository.RawMaterialRepository;
import br.com.projedata.service.RawMaterialService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RawMaterialSertviceTest {

    @InjectMocks
    private RawMaterialService rawMaterialService;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private ProductCompositionRepository productCompositionRepository;

    @Mock
    private ModelMapper modelMapper;


    @Test
    @DisplayName("Should create a raw material successfully")
    void shouldCreateRawMaterialSuccessfully() {
        RawMaterialDTO inputDto = new RawMaterialDTO();
        inputDto.setName("Wood");
        inputDto.setStockQuantity(100.0);
        RawMaterial materialEntity = new RawMaterial();
        materialEntity.setName("Wood");
        materialEntity.setStockQuantity(100.0);
        RawMaterial savedMaterial = new RawMaterial();
        savedMaterial.setId(1L);
        savedMaterial.setName("Wood");
        savedMaterial.setStockQuantity(100.0);
        RawMaterialDTO expectedDto = new RawMaterialDTO();
        expectedDto.setId(1L);
        expectedDto.setName("Wood");
        expectedDto.setStockQuantity(100.0);
        when(modelMapper.map(inputDto, RawMaterial.class)).thenReturn(materialEntity);
        when(rawMaterialRepository.save(materialEntity)).thenReturn(savedMaterial);
        when(modelMapper.map(savedMaterial, RawMaterialDTO.class)).thenReturn(expectedDto);
        RawMaterialDTO result = rawMaterialService.createRawMaterial(inputDto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Wood", result.getName());
        verify(rawMaterialRepository, times(1)).save(any(RawMaterial.class));
    }


    @Test
    @DisplayName("Should throw exception when repository save fails")
    void shouldThrowExceptionWhenSaveFails() {
        RawMaterialDTO inputDto = new RawMaterialDTO();
        inputDto.setName("Iron");
        inputDto.setStockQuantity(10.0);

        RawMaterial materialEntity = new RawMaterial();

        when(modelMapper.map(any(), eq(RawMaterial.class))).thenReturn(materialEntity);
        when(rawMaterialRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            rawMaterialService.createRawMaterial(inputDto);
        });
    }

    @Test
    @DisplayName("Should return a list of all raw materials successfully")
    void shouldReturnListOfRawMaterialsSuccessfully() {

        RawMaterial rm1 = new RawMaterial(1L, "Wood", 100.0, new ArrayList<>());
        RawMaterial rm2 = new RawMaterial(2L, "Steel", 50.0, new ArrayList<>());

        RawMaterialDTO dto1 = new RawMaterialDTO(1L, "Wood", 100.0,new ArrayList<>());
        RawMaterialDTO dto2 = new RawMaterialDTO(2L, "Steel", 50.0, new ArrayList<>());

        when(rawMaterialRepository.findAll()).thenReturn(List.of(rm1, rm2));
        when(modelMapper.map(rm1, RawMaterialDTO.class)).thenReturn(dto1);
        when(modelMapper.map(rm2, RawMaterialDTO.class)).thenReturn(dto2);

        List<RawMaterialDTO> result = rawMaterialService.listRawMaterials();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Wood", result.get(0).getName());
        assertEquals("Steel", result.get(1).getName());
        verify(rawMaterialRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return a RawMaterialDTO when ID exists")
    void shouldReturnRawMaterialByIdSuccessfully() {

        Long id = 1L;
        RawMaterial material = new RawMaterial(id, "Wood", 100.0, new ArrayList<>());
        RawMaterialDTO expectedDto = new RawMaterialDTO(id, "Wood", 100.0, new ArrayList<>());
        when(rawMaterialRepository.findById(id)).thenReturn(Optional.of(material));
        when(modelMapper.map(material, RawMaterialDTO.class)).thenReturn(expectedDto);
        RawMaterialDTO result = rawMaterialService.getRawMaterialById(id);
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Wood", result.getName());
        verify(rawMaterialRepository, times(1)).findById(id);
        verify(modelMapper, times(1)).map(material, RawMaterialDTO.class);
    }

    @Test
    @DisplayName("Should throw RawMaterialNotFoundException when ID does not exist")
    void shouldThrowExceptionWhenRawMaterialNotFound() {
        Long nonExistentId = 99L;
        when(rawMaterialRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        assertThrows(RawMaterialNotFoundException.class, () -> {
            rawMaterialService.getRawMaterialById(nonExistentId);
        });

        verify(rawMaterialRepository, times(1)).findById(nonExistentId);
        verifyNoInteractions(modelMapper);
    }

    @Test
    @DisplayName("Should update raw material successfully when ID exists")
    void shouldUpdateRawMaterialSuccessfully() {
        Long id = 1L;
        RawMaterialDTO updateRequestDto = new RawMaterialDTO(id, "Updated Wood", 150.0, new ArrayList<>());
        RawMaterial existingMaterial = new RawMaterial(id, "Old Wood", 100.0, new ArrayList<>());
        RawMaterial savedMaterial = new RawMaterial(id, "Updated Wood", 150.0, new ArrayList<>());
        RawMaterialDTO expectedDto = new RawMaterialDTO(id, "Updated Wood", 150.0, new ArrayList<>());
        when(rawMaterialRepository.findById(id)).thenReturn(Optional.of(existingMaterial));
        doAnswer(invocation -> {

            return null;
        }).when(modelMapper).map(any(RawMaterialDTO.class), any(RawMaterial.class));
        when(rawMaterialRepository.save(any(RawMaterial.class))).thenReturn(savedMaterial);
        when(modelMapper.map(any(RawMaterial.class), eq(RawMaterialDTO.class))).thenReturn(expectedDto);
        RawMaterialDTO result = rawMaterialService.updateRawMaterial(id, updateRequestDto);
        assertNotNull(result);
        assertEquals("Updated Wood", result.getName());
        verify(rawMaterialRepository).save(any(RawMaterial.class));
    }


    @Test
    @DisplayName("Should throw RawMaterialNotFoundException when updating non-existent ID")
    void shouldThrowExceptionWhenUpdateIdDoesNotExist() {
        Long nonExistentId = 99L;
        RawMaterialDTO updateDto = new RawMaterialDTO(nonExistentId, "New Name", 200.0, new ArrayList<>());
        when(rawMaterialRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        assertThrows(RawMaterialNotFoundException.class, () -> {
            rawMaterialService.updateRawMaterial(nonExistentId, updateDto);
        });

        verify(rawMaterialRepository, times(1)).findById(nonExistentId);
        verify(rawMaterialRepository, never()).save(any(RawMaterial.class));
        verifyNoInteractions(modelMapper);
    }

    @Test
    @DisplayName("It should throw a RawMaterialNotFoundException when deleting a non-existent ID and verify the deletion of compositions.")
    void shouldThrowExceptionWhenDeleteIdDoesNotExist() {
        Long nonExistentId = 99L;
        when(rawMaterialRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        assertThrows(RawMaterialNotFoundException.class, () -> {
            rawMaterialService.deleteRawMaterial(nonExistentId);
        });
        verify(productCompositionRepository, times(1)).deleteByRawMaterialId(nonExistentId);
        verify(rawMaterialRepository, times(1)).findById(nonExistentId);
        verify(rawMaterialRepository, never()).delete(any(RawMaterial.class));
    }


}