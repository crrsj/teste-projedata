package br.com.projedata.controller;

import br.com.projedata.dto.RawMaterialDTO;
import br.com.projedata.service.RawMaterialService;
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
@RequestMapping("/api/material")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;


    @PostMapping
    @Operation(summary = "Endpoint responsible for registering the raw material.")
    @ApiResponse(responseCode = "201", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
    public ResponseEntity<RawMaterialDTO>createRawMaterial(@RequestBody @Valid RawMaterialDTO rawMaterialDTO){
        var rawMaterial = rawMaterialService.createRawMaterial(rawMaterialDTO);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(rawMaterial.getId()).toUri();
        return ResponseEntity.created(uri).body(rawMaterial);
    }

    @GetMapping
    @Operation(summary = "Endpoint responsible for listing the raw materials.")
    @ApiResponse(responseCode = "200", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
    public ResponseEntity<List<RawMaterialDTO>>listRawMaterials(){
        return ResponseEntity.ok(rawMaterialService.listRawMaterials());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Endpoint responsible for searching for raw materials by id.")
    @ApiResponse(responseCode = "200", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
    public ResponseEntity<RawMaterialDTO>getRawMaterialById(@PathVariable Long id){
        return ResponseEntity.ok(rawMaterialService.getRawMaterialById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Endpoint responsible for updating raw materials.")
    @ApiResponse(responseCode = "200", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
    public ResponseEntity<RawMaterialDTO>updaterawMaterial(@PathVariable Long id, @RequestBody @Valid RawMaterialDTO rawMaterialDTO ){
        return ResponseEntity.ok(rawMaterialService.updateRawMaterial(id, rawMaterialDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Endpoint responsible for deleting the raw material.")
    @ApiResponse(responseCode = "204", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
    public ResponseEntity<Void>deleteRawMaterial(@PathVariable Long id){
        rawMaterialService.deleteRawMaterial(id);
        return ResponseEntity.noContent().build();
    }
}
