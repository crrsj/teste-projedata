package br.com.projedata;

import br.com.projedata.dto.ProductCompositionDTO;
import br.com.projedata.dto.ProductionSuggestionDTO;
import br.com.projedata.entity.Product;
import br.com.projedata.entity.ProductComposition;
import br.com.projedata.entity.RawMaterial;
import br.com.projedata.exceptions.ProductNotFoundException;
import br.com.projedata.exceptions.RawMaterialNotFoundException;
import br.com.projedata.repository.ProductCompositionRepository;
import br.com.projedata.repository.ProductRepository;
import br.com.projedata.repository.RawMaterialRepository;
import br.com.projedata.service.ProductCompositionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductCompositionServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private ProductCompositionRepository compositionRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductCompositionService compositionService;

    @Test
    @DisplayName("Should save composition successfully")
    void shouldSaveCompositionSuccessfully() {
        Long productId = 1L;
        Long rawMaterialId = 2L;
        ProductCompositionDTO inputDto = new ProductCompositionDTO();
        Product product = new Product();
        product.setId(productId);
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setId(rawMaterialId);
        ProductComposition composition = new ProductComposition();
        ProductComposition savedComposition = new ProductComposition();
        ProductCompositionDTO expectedDto = new ProductCompositionDTO();
        when(modelMapper.map(any(ProductCompositionDTO.class), eq(ProductComposition.class))).thenReturn(composition);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(rawMaterialRepository.findById(rawMaterialId)).thenReturn(Optional.of(rawMaterial));
        when(compositionRepository.save(any(ProductComposition.class))).thenReturn(savedComposition);
        when(modelMapper.map(any(ProductComposition.class), eq(ProductCompositionDTO.class))).thenReturn(expectedDto);
        ProductCompositionDTO result = compositionService.saveComposition(productId, rawMaterialId, inputDto);
        assertNotNull(result);
        verify(compositionRepository).save(any(ProductComposition.class));
        verify(productRepository).findById(productId);
        verify(rawMaterialRepository).findById(rawMaterialId);
    }

    @Test
    @DisplayName("Should calculate production suggestions based on stock")
    void shouldCalculateSuggestionSuccessfully() {
        Product product = new Product();
        product.setName("Luxury Table");
        product.setPrice(BigDecimal.valueOf(1000.0));

        RawMaterial wood = new RawMaterial();
        wood.setId(1L);
        wood.setStockQuantity(100.0);

        ProductComposition composition = new ProductComposition();
        composition.setRawMaterial(wood);
        composition.setQuantity(10.0);

        product.setCompositions(List.of(composition));

        when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));
        when(rawMaterialRepository.findAll()).thenReturn(new ArrayList<>(List.of(wood)));

        List<ProductionSuggestionDTO> results = compositionService.calculateSuggestion();

        assertFalse(results.isEmpty());
        assertEquals("Luxury Table", results.get(0).getProductName());
        assertEquals(10, results.get(0).getQuantity());
        assertEquals(0, BigDecimal.valueOf(10000.0).compareTo(results.get(0).getTotalValue()));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product does not exist")
    void shouldThrowExceptionWhenProductNotFound() {
        Long productId = 1L;
        ProductCompositionDTO dto = new ProductCompositionDTO();
        when(modelMapper.map(any(), any())).thenReturn(new ProductComposition());
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> {
            compositionService.saveComposition(productId, 2L, dto);
        });

        verify(compositionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return empty list when no stock is available for production")
    void shouldReturnEmptyWhenNoStock() {
        Product product = new Product();
        product.setCompositions(new ArrayList<>());
        when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));
        when(rawMaterialRepository.findAll()).thenReturn(new ArrayList<>(List.of(new RawMaterial())));
        List<ProductionSuggestionDTO> results = compositionService.calculateSuggestion();
        assertTrue(results.isEmpty());
    }



    @Test
    @DisplayName("Should throw RawMaterialNotFoundException when raw material id does not exist")
    void shouldThrowExceptionWhenRawMaterialNotFound() {
        Long productId = 1L;
        Long rawMaterialId = 99L;
        ProductCompositionDTO dto = new ProductCompositionDTO();
        Product product = new Product();
        when(modelMapper.map(any(), any())).thenReturn(new ProductComposition());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(rawMaterialRepository.findById(rawMaterialId)).thenReturn(Optional.empty());
        assertThrows(RawMaterialNotFoundException.class, () -> {
            compositionService.saveComposition(productId, rawMaterialId, dto);
        });

        verify(compositionRepository, never()).save(any());
    }
}
