package ADA.productservice.repository;

import ADA.productservice.entity.ListedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ListedProductRepository extends JpaRepository<ListedProduct, Integer> {

    List<ListedProduct> findAllByUser_Id(Integer userId);

    boolean existsByUser_IdAndProduct_Id(Integer userId, Integer productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ListedProduct l WHERE l.user.id = :userId AND l.product.id = :productId")
    void deleteByUser_IdAndProduct_Id(@Param("userId") Integer userId, @Param("productId") Integer productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ListedProduct l WHERE l.product.id = :productId")
    void deleteAllByProduct_Id(@Param("productId") Integer productId);
}
