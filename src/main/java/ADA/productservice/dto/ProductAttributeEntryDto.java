package ADA.productservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductAttributeEntryDto {
    private Integer attributeId;
    private String attributeName;
    private Integer valueId;
    private String valueName;
}
