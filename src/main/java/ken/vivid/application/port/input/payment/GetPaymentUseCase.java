package ken.vivid.application.port.input.payment;

import ken.vivid.domain.entities.payment.Payment;

import java.util.List;

public interface GetPaymentUseCase {
    Payment getById(Long id);
    List<Payment> getByOrderId(Long orderId);
}
