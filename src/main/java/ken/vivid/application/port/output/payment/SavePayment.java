package ken.vivid.application.port.output.payment;

import ken.vivid.domain.entities.payment.Payment;

public interface SavePayment {
    Payment save(Payment payment);
}
