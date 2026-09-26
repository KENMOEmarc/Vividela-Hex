package ken.vivid.application.port.input.payment;

import ken.vivid.domain.entities.payment.Payment;

public interface ConfirmPaymentUseCase {
    Payment confirm(Long paymentId, Long actingUserId);
}
