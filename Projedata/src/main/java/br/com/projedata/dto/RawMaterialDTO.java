package br.com.projedata.dto;

import br.com.projedata.entity.ProductComposition;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RawMaterialDTO {
    private Long id;
    @NotBlank(message = "Raw material name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Double stockQuantity;
    private List<ProductComposition> compositions = new ArrayList<>();

}
