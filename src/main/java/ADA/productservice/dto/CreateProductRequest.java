package ADA.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductRequest {

    @NotBlank
    private String name;

    @NotNull
    private Integer categoryId;

    @NotNull
    @Positive
    private BigDecimal price;

    private String description;

    @NotBlank
    private String region;

    private List<AttributePairRequest> attributes;

    private List<String> imageUrls;
}
