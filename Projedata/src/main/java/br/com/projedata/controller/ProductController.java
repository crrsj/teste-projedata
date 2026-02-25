package br.com.projedata.controller;

import br.com.projedata.dto.ProductDTO;
import br.com.projedata.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Endpoint responsible for registering products.")
    @ApiResponse(responseCode = "201", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
    public ResponseEntity<ProductDTO>createProduct(@RequestBody @Valid ProductDTO productDTO){
        var product = productService.createProduct(productDTO);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id]")
                .buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(uri).body(product);
    }

    @GetMapping
    @Operation(summary = "Endpoint responsible for listing products.")
    @ApiResponse(responseCode = "200", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
    public ResponseEntity<List<ProductDTO>>listProducts(){
        return ResponseEntity.ok(productService.ListProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Endpoint responsible for searching for products by id.")
    @ApiResponse(responseCode = "200", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
    public ResponseEntity<ProductDTO>getProductById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Endpoint responsible for updating the product.")
    @ApiResponse(responseCode = "200", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
    public ResponseEntity<ProductDTO>updateProducts(@PathVariable Long id, @RequestBody @Valid ProductDTO productDTO){
        return ResponseEntity.ok(productService.updateProducts(id, productDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Endpoint responsible for deleting product.")
    @ApiResponse(responseCode = "204", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
    public ResponseEntity<Void>deleteProduct(@PathVariable Long id){
        productService.deleteProducts(id);
        return ResponseEntity.noContent().build();
    }
}
