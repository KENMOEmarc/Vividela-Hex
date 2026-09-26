package ken.vivid.application.port.input.payment;

import ken.vivid.domain.entities.payment.Payment;

public interface FailPaymentUseCase {
    Payment fail(Long paymentId, Long actingUserId);
}
