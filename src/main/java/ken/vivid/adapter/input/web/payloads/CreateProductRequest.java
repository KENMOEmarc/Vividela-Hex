package ken.vivid.adapter.input.web.payloads;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ken.vivid.domain.dto.MeasurementUnit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductRequest {

    @NotBlank(message = "The product name is required")
    @Size(max = 100, message = "The product name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "The threshold value is required")
    @DecimalMin(value = "0", message = "The threshold value cannot be negative")
    private BigDecimal thresholdValue;

    @NotNull(message = "The measurement unit is required")
    private MeasurementUnit measurementUnit;
}
