package ADA.productservice.repository;

import ADA.productservice.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Integer> {
}
