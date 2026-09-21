package ken.vivid.adapter.input.web.payloads.order;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateOrderRequest {
    @NotNull private Long clientId;
    @FutureOrPresent private LocalDate expectedDeliveryDate;
    @Size(max = 1000) private String notes;
    @NotNull @DecimalMin("0.00") private BigDecimal totalAmount;
    @NotNull @DecimalMin("0.00") private BigDecimal discountAmount;
}
