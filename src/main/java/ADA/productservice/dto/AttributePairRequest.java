package ADA.productservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttributePairRequest {

    @NotNull
    private Integer attributeId;

    @NotNull
    private Integer attributeValueId;
}
