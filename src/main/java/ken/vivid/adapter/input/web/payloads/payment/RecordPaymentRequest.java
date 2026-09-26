package ken.vivid.adapter.input.web.payloads.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ken.vivid.domain.entities.payment.PaymentMethodType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecordPaymentRequest {
    @NotNull(message = "The order id is required")
    private Long orderId;

    @NotNull(message = "The payment method is required")
    private PaymentMethodType paymentMethod;

    @NotNull(message = "The payment amount is required")
    @DecimalMin(value = "0.01", message = "The payment amount must be greater than zero")
    private BigDecimal amount;

    @Size(max = 20, message = "The payer phone cannot exceed 20 characters")
    private String payerPhone;

    @Size(max = 255, message = "The transaction reference cannot exceed 255 characters")
    private String transactionReference;
}
