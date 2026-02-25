package br.com.projedata.service;

import br.com.projedata.dto.ProductCompositionDTO;
import br.com.projedata.dto.ProductionSuggestionDTO;
import br.com.projedata.entity.ProductComposition;
import br.com.projedata.entity.RawMaterial;
import br.com.projedata.entity.Product;
import br.com.projedata.excessoes.RawMaterialNotFoundException;
import br.com.projedata.excessoes.ProductNotFoundException;
import br.com.projedata.repository.ProductCompositionRepository;
import br.com.projedata.repository.RawMaterialRepository;
import br.com.projedata.repository.ProductRepository;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
@RequiredArgsConstructor
public class ProductCompositionService {

        private final ProductRepository productRepository;
        private final RawMaterialRepository rawMaterialRepository;
        private final ProductCompositionRepository compositionRepository;
        private final ModelMapper modelMapper;

        public ProductCompositionDTO saveComposition(Long productId, Long rawMaterialId, ProductCompositionDTO dto) {
            var composition = modelMapper.map(dto, ProductComposition.class);

            var product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Error: Product not found with id: " + productId));

            var rawMaterial = rawMaterialRepository.findById(rawMaterialId)
                    .orElseThrow(() -> new RawMaterialNotFoundException("Error: Raw material not found with id: " + rawMaterialId));

            composition.setProduct(product);
            composition.setRawMaterial(rawMaterial);

            ProductComposition saved = compositionRepository.save(composition);
            return modelMapper.map(saved, ProductCompositionDTO.class);
        }

        public List<ProductionSuggestionDTO> calculateSuggestion() {
            List<Product> products = productRepository.findAll();
            List<RawMaterial> simulatedStock = rawMaterialRepository.findAll();
            List<ProductionSuggestionDTO> results = new ArrayList<>();

            products.sort((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));
            for (Product product : products) {
                if (product.getCompositions() == null || product.getCompositions().isEmpty()) continue;

                int maxPossible = Integer.MAX_VALUE;
                for (ProductComposition comp : product.getCompositions()) {
                    RawMaterial rm = simulatedStock.stream()
                            .filter(m -> m.getId().equals(comp.getRawMaterial().getId()))
                            .findFirst()
                            .orElse(null);

                    if (rm == null || rm.getStockQuantity() <= 0) {
                        maxPossible = 0;
                        break;
                    }

                    int capacity = (int) (rm.getStockQuantity() / comp.getQuantity());
                    if (capacity < maxPossible) maxPossible = capacity;
                }

                if (maxPossible > 0 && maxPossible != Integer.MAX_VALUE) {
                    for (ProductComposition comp : product.getCompositions()) {
                        for (RawMaterial rm : simulatedStock) {
                            if (rm.getId().equals(comp.getRawMaterial().getId())) {
                                double totalSpent = maxPossible * comp.getQuantity();
                                rm.setStockQuantity(rm.getStockQuantity() - totalSpent);
                            }
                        }
                    }

                    BigDecimal totalFinancialValue = product.getPrice().multiply(BigDecimal.valueOf(maxPossible));
                    results.add(new ProductionSuggestionDTO(product.getName(), maxPossible, totalFinancialValue));
                }
            }

            return results;
        }


}