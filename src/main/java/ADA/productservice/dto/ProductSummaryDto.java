package ADA.productservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductSummaryDto {
    private Integer id;
    private String name;
    private Integer categoryId;
    private BigDecimal price;
    private List<Integer> attributeIds;
    private boolean inStock;
    private String thumbnailUrl;
    private String region;
}
