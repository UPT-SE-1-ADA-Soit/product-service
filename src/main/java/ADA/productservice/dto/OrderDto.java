package ADA.productservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class OrderDto {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String productName;
    private OffsetDateTime orderedAt;
}