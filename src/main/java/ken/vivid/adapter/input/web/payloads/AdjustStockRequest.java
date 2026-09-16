package ken.vivid.adapter.input.web.payloads;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdjustStockRequest {

    @NotNull(message = "The new stock level is required")
    @DecimalMin(value = "0", message = "The new stock level cannot be negative")
    private BigDecimal newStockLevel;

    private String notes;
}
