package ADA.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttributeValueRequest {

    @NotBlank
    private String name;
}
