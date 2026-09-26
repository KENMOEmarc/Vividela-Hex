package ken.vivid.application.port.input.payment.recordPayment;

import ken.vivid.domain.entities.payment.Payment;

public interface RecordPaymentUseCase {
    Payment record(RecordPaymentCommand command);
}
