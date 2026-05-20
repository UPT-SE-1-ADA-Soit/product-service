package ADA.productservice.repository;

import ADA.productservice.entity.AttributeValuePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AttributeValuePairRepository extends JpaRepository<AttributeValuePair, Integer> {

    List<AttributeValuePair> findAllByProduct_Id(Integer productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM AttributeValuePair p WHERE p.product.id = :productId")
    void deleteAllByProduct_Id(@Param("productId") Integer productId);
}
