package ADA.productservice.repository;

import ADA.productservice.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    List<Favorite> findAllByUser_Id(Integer userId);

    boolean existsByUser_IdAndProduct_Id(Integer userId, Integer productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user.id = :userId AND f.product.id = :productId")
    void deleteByUser_IdAndProduct_Id(@Param("userId") Integer userId, @Param("productId") Integer productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.product.id = :productId")
    void deleteAllByProduct_Id(@Param("productId") Integer productId);
}