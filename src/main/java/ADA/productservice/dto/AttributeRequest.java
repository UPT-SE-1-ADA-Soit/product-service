package ADA.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttributeRequest {

    @NotBlank
    private String name;
}
