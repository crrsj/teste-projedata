package br.com.projedata.service;

import br.com.projedata.dto.RawMaterialDTO;
import br.com.projedata.entity.ProductComposition;
import br.com.projedata.entity.RawMaterial;
import br.com.projedata.excessoes.RawMaterialNotFoundException;
import br.com.projedata.repository.ProductCompositionRepository;
import br.com.projedata.repository.RawMaterialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RawMaterialService {

    private final ModelMapper modelMapper;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductCompositionRepository compositionRepository;

    @Transactional
    public RawMaterialDTO createRawMaterial(RawMaterialDTO rawMaterialDTO){
        var material = modelMapper.map(rawMaterialDTO, RawMaterial.class);
        var savedMaterial = rawMaterialRepository.save(material);
        return modelMapper.map(savedMaterial, RawMaterialDTO.class);
    }

    public List<RawMaterialDTO> listRawMaterials(){
        return rawMaterialRepository.findAll( )
                .stream().map(material -> modelMapper.map(material, RawMaterialDTO.class)).toList();


    }

    public RawMaterialDTO getRawMaterialById(Long id){
        var rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RawMaterialNotFoundException("Error: Raw material not found with id: " + id));
        return modelMapper.map(rawMaterial, RawMaterialDTO.class);
    }

    @Transactional
    public RawMaterialDTO updateRawMaterial(Long id, RawMaterialDTO rawMaterialDTO){
        var rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RawMaterialNotFoundException("Error: Raw material not found with id: " + id));
        modelMapper.map(rawMaterialDTO, rawMaterial);
        var updatedRawMaterial = rawMaterialRepository.save(rawMaterial);
        return modelMapper.map(updatedRawMaterial, RawMaterialDTO.class);
    }

    @Transactional
    public void deleteRawMaterial(Long id){
        compositionRepository.deleteByRawMaterialId(id);
        var rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RawMaterialNotFoundException("Error: Raw material not found with id: " + id));

        rawMaterialRepository.delete(rawMaterial);
    }
}
