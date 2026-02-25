package br.com.projedata.controller;

import br.com.projedata.dto.ProductCompositionDTO;
import br.com.projedata.dto.ProductionSuggestionDTO;
import br.com.projedata.service.ProductCompositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/composition")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductCompositionController {

    private final ProductCompositionService compositionService;


    @PostMapping("/{productId}/{rawMaterialId}")
    @Operation(summary = "The endpoint that establishes the manufacturing structure of a product, linking it to its raw materials and defining the quantities needed to produce one unit.")
    @ApiResponse(responseCode = "201", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
    public ResponseEntity<ProductCompositionDTO>saveComposition(@PathVariable Long productId, @PathVariable Long rawMaterialId, @RequestBody ProductCompositionDTO dto){
      return ResponseEntity.status(HttpStatus.CREATED).body(compositionService.saveComposition(productId,rawMaterialId,dto));
    }

    @GetMapping("/calculate")
    @Operation(summary = "The endpoint responsible for performing intelligent analysis of the current raw material inventory and generating a list of suggested products to be manufactured.")
    @ApiResponse(responseCode = "200", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
   public ResponseEntity<List<ProductionSuggestionDTO>>productionSuggestions(){
        return ResponseEntity.ok(compositionService.calculateSuggestion());

    }

}
