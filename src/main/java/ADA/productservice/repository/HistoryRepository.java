package ADA.productservice.repository;

import ADA.productservice.entity.History;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Integer> {

    List<History> findAllByUser_IdOrderByIdAsc(Integer userId);

    List<History> findTop15ByUser_IdOrderByIdDesc(Integer userId);

    boolean existsByUser_IdAndProduct_Id(Integer userId, Integer productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM History h WHERE h.user.id = :userId AND h.product.id = :productId")
    void deleteByUser_IdAndProduct_Id(@Param("userId") Integer userId, @Param("productId") Integer productId);

    @Query("SELECT p.category.id FROM History h JOIN h.product p WHERE h.user.id = :userId GROUP BY p.category.id ORDER BY COUNT(p.category.id) DESC")
    List<Integer> findTopCategoryIdsByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM History h WHERE h.product.id = :productId")
    void deleteAllByProduct_Id(@Param("productId") Integer productId);
}
