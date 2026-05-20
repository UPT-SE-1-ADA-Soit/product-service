package ADA.productservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attribute_value_pair")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValuePair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_value_id", nullable = false)
    private AttributeValue attributeValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
