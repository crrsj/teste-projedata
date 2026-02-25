package br.com.projedata.service;

import br.com.projedata.dto.ProductDTO;
import br.com.projedata.entity.Product;
import br.com.projedata.exceptions.ProductNotFoundException;
import br.com.projedata.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    @Transactional
    public ProductDTO createProduct(ProductDTO produtoDTO){
        var product = modelMapper.map(produtoDTO, Product.class);
        var savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    public List<ProductDTO> ListProducts(){
        return productRepository.findAll()
                .stream().map(products -> modelMapper
                .map(products, ProductDTO.class)).toList();

    }

    public ProductDTO getProductById(Long id){
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Error: Product not found with id: " + id));

        return modelMapper.map(product, ProductDTO.class);
    }

   @Transactional
   public ProductDTO updateProducts(Long id, ProductDTO produtoDTO){
       var product = productRepository.findById(id)
               .orElseThrow(()-> new ProductNotFoundException("Error: Product not found with id: " + id + " does not exist."));
       modelMapper.map(produtoDTO,product);
       var newProduct = productRepository.save(product);
       return modelMapper.map(newProduct, ProductDTO.class);
   }

   @Transactional
   public void deleteProducts(Long id){
       var produto = productRepository.findById(id)
               .orElseThrow(()-> new ProductNotFoundException("Error: Product not found with id: " + id + " does not exist."));
       productRepository.delete(produto);
   }
}
