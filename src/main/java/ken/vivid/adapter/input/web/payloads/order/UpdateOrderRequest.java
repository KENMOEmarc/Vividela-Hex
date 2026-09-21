package ken.vivid.adapter.input.web.payloads.order;

import jakarta.validation.constraints.*;
import ken.vivid.domain.entities.order.OrderStatus;
import ken.vivid.domain.entities.order.PaymentStatus;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateOrderRequest {
    @FutureOrPresent private LocalDate expectedDeliveryDate;
    @NotNull private OrderStatus status;
    @NotNull private PaymentStatus paymentStatus;
    @Size(max = 1000) private String notes;
    @NotNull @DecimalMin("0.00") private BigDecimal totalAmount;
}
