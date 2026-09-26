package ken.vivid.adapter.input.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ken.vivid.domain.entities.payment.Payment;
import ken.vivid.domain.entities.payment.PaymentMethodType;
import ken.vivid.domain.entities.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentDto(Long id, Long orderId, PaymentMethodType paymentMethod, BigDecimal amount,
                        String payerPhone, String transactionReference, PaymentStatus status,
                        Instant paidAt, Long createdBy, Instant createdAt, Instant updatedAt) {
    public static PaymentDto from(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getOrderId(),
                payment.getPaymentMethod(),
                payment.getAmount(),
                payment.getPayerPhone(),
                payment.getTransactionReference(),
                payment.getStatus(),
                payment.getPaidAt(),
                payment.getCreatedBy(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
