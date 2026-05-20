package ADA.productservice.spec;

import ADA.productservice.entity.AttributeValuePair;
import ADA.productservice.entity.Product;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpec {

    private ProductSpec() {}

    public static Specification<Product> withFilters(
            Integer categoryId,
            BigDecimal priceMin,
            BigDecimal priceMax,
            String searchQuery,
            List<Integer> recommendedCategoryIds) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            } else if (recommendedCategoryIds != null && !recommendedCategoryIds.isEmpty()) {
                predicates.add(root.get("category").get("id").in(recommendedCategoryIds));
            }

            if (priceMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), priceMin));
            }
            if (priceMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), priceMax));
            }
            if (searchQuery != null && !searchQuery.isBlank()) {
                String pattern = "%" + searchQuery.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Product> hasAllAttributes(List<Integer> attributeIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Integer attrId : attributeIds) {
                Subquery<Integer> sub = query.subquery(Integer.class);
                Root<AttributeValuePair> pairRoot = sub.from(AttributeValuePair.class);
                sub.select(pairRoot.get("product").get("id"))
                        .where(
                                cb.equal(pairRoot.get("product").get("id"), root.get("id")),
                                cb.equal(pairRoot.get("attribute").get("id"), attrId)
                        );
                predicates.add(cb.exists(sub));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
