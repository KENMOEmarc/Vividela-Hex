package ken.vivid.adapter.input.web.payloads.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ConsumeStockRequest {

    @NotNull(message = "The product id is required")
    private Long productId;

    @NotNull(message = "The quantity is required")
    @DecimalMin(value = "0.01", message = "The quantity must be strictly positive")
    private BigDecimal quantity;

    private String notes;
}
