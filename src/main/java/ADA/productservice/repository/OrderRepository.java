package ADA.productservice.repository;

import ADA.productservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findAllByUser_Id(Integer userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Order o WHERE o.product.id = :productId")
    void deleteAllByProduct_Id(@Param("productId") Integer productId);
}