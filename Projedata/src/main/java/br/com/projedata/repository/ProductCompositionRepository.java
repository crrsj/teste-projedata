package br.com.projedata.repository;

import br.com.projedata.entity.ProductComposition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCompositionRepository extends JpaRepository<ProductComposition,Long> {
    void deleteByRawMaterialId(Long materialId);


}
