package br.com.projedata.repository;

import br.com.projedata.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawMaterialRepository extends JpaRepository<RawMaterial,Long> {
}
