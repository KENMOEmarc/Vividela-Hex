package ken.vivid.application.port.input.payment.recordPayment;

import ken.vivid.domain.entities.payment.PaymentMethodType;

import java.math.BigDecimal;

public record RecordPaymentCommand(Long orderId, PaymentMethodType paymentMethod, BigDecimal amount,
                                   String payerPhone, String transactionReference, Long actingUserId) {
}
