package br.com.projedata.dto;

import br.com.projedata.entity.ProductComposition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDTO {
    private Long id;
    @NotBlank(message = "Product name is required")
    private String name;
    @NotNull(message = "Sale price is required")
    @Positive(message = "Sale price must be greater than zero")
    private BigDecimal price;
    private List<ProductComposition> compositions = new ArrayList<>();
}
