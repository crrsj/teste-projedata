package br.com.projedata.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductionSuggestionDTO {

    private String productName;
    private Integer quantity;
    private BigDecimal totalValue;
}
