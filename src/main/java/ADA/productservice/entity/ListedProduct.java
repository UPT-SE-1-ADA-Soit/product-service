package ADA.productservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "listed_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
