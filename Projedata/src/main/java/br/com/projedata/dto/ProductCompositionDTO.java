package br.com.projedata.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductCompositionDTO {

    @NotNull(message = "Product ID is required")
    private Long productId;
    @NotNull(message = "Raw material ID is required")
    private Long rawMaterialId;
    private String productName;
    @NotNull(message = "Required quantity is mandatory")
    @Positive(message = "Quantity must be at least 1")
    private Double quantity;
}
