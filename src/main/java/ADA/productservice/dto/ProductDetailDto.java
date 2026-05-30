package ADA.productservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProductDetailDto {
    private Integer id;
    private String name;
    private Integer categoryId;
    private String categoryName;
    private BigDecimal price;
    private String description;
    private LocalDate addedDate;
    private String region;
    private List<ProductAttributeEntryDto> attributes;
    private boolean inStock;
    private Integer sellerId;
    private List<String> imageUrls;
}
