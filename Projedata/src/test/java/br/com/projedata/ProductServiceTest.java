package br.com.projedata;

import br.com.projedata.dto.ProductDTO;
import br.com.projedata.entity.Product;
import br.com.projedata.excessoes.ProductNotFoundException;
import br.com.projedata.repository.ProductRepository;
import br.com.projedata.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {

        product = new Product(1L, "Finished Chair", BigDecimal.valueOf(50.0), new ArrayList<>());
        productDTO = new ProductDTO(1L, "Finished Chair", BigDecimal.valueOf(50.0), new ArrayList<>());
    }
    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {

        when(modelMapper.map(any(ProductDTO.class), eq(Product.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO);
        ProductDTO result = productService.createProduct(productDTO);
        assertNotNull(result);
        assertEquals("Finished Chair", result.getName());
        assertEquals(0, BigDecimal.valueOf(50.0).compareTo(result.getPrice()));
    }

    @Test
    @DisplayName("Should throw exception when database fails to save")
    void shouldThrowExceptionWhenRepositoryFails() {
        when(modelMapper.map(any(ProductDTO.class), eq(Product.class))).thenReturn(product);
        when(productRepository.save(any(Product.class)))
                .thenThrow(new DataIntegrityViolationException("Database error"));
        assertThrows(DataIntegrityViolationException.class, () -> {
            productService.createProduct(productDTO);
        });
    }

    @Test
    @DisplayName("Should return list of products")
    void shouldReturnListOfProducts() {

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO);
        List<ProductDTO> result = productService.ListProducts();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(1L);
        });
    }

    @Test
    @DisplayName("Should return a ProductDTO when searching by an existing ID")
    void shouldReturnProductDtoWhenIdExists() {

        Long id = 1L;
        Product foundProduct = new Product(id, "Finished Chair", BigDecimal.valueOf(50.0), new ArrayList<>());
        ProductDTO expectedDto = new ProductDTO(id, "Finished Chair", BigDecimal.valueOf(50.0), new ArrayList<>());
        when(productRepository.findById(id)).thenReturn(Optional.of(foundProduct));
        when(modelMapper.map(foundProduct, ProductDTO.class)).thenReturn(expectedDto);
        ProductDTO result = productService.getProductById(id);
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Finished Chair", result.getName());
        verify(productRepository, times(1)).findById(id);
        verify(modelMapper, times(1)).map(foundProduct, ProductDTO.class);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when searching for a non-existent ID")
    void shouldThrowExceptionWhenIdDoesNotExist() {
        Long id = 99L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(id);
        });

        assertTrue(exception.getMessage().contains(String.valueOf(id)));
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating non-existent ID")
    void shouldThrowExceptionWhenUpdatingNonExistentId() {

        Long id = 99L;
        ProductDTO updateDto = new ProductDTO(null, "New Name", BigDecimal.valueOf(100.0), new ArrayList<>());
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProducts(id, updateDto);
        });

        verify(productRepository, never()).save(any(Product.class));
        verify(modelMapper, never()).map(any(), eq(ProductDTO.class));
    }
    @Test
    @DisplayName("Should update product successfully when ID exists")
    void shouldUpdateProductSuccessfully() {

        Long id = 1L;
        ProductDTO updateRequestDto = new ProductDTO(id, "Updated Table", BigDecimal.valueOf(300.0), new ArrayList<>());
        Product existingProduct = new Product(id, "Old Table", BigDecimal.valueOf(150.0), new ArrayList<>());
        Product savedProduct = new Product(id, "Updated Table", BigDecimal.valueOf(300.0), new ArrayList<>());
        ProductDTO expectedDto = new ProductDTO(id, "Updated Table", BigDecimal.valueOf(300.0), new ArrayList<>());
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        doAnswer(invocation -> {
            return null;
        }).when(modelMapper).map(any(ProductDTO.class), any(Product.class));
        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(expectedDto);
        ProductDTO result = productService.updateProducts(id, updateRequestDto);
        assertNotNull(result);
        assertEquals("Updated Table", result.getName());
        assertEquals(0, BigDecimal.valueOf(300.0).compareTo(result.getPrice()));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating non-existent product")
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {

        Long id = 99L;
        ProductDTO updateRequestDto = new ProductDTO(id, "Ghost Product", BigDecimal.valueOf(500.0), new ArrayList<>());
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProducts(id, updateRequestDto);
        });

        verify(productRepository, never()).save(any(Product.class));
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    @DisplayName("Should delete product successfully when ID exists")
    void shouldDeleteProductSuccessfully() {

        Long id = 1L;
        Product product = new Product(id, "Cadeira Gamer", BigDecimal.valueOf(500.0), new ArrayList<>());
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(any(Product.class));
        productService.deleteProducts(id);
        verify(productRepository, times(1)).delete(any(Product.class));
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when deleting non-existent product")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        Long id = 99L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProducts(id);
        });

        verify(productRepository, never()).delete(any(Product.class));
        verify(productRepository, never()).deleteById(anyLong());
    }
}