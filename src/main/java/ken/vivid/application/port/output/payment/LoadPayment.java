package ken.vivid.application.port.output.payment;

import ken.vivid.adapter.output.persistence.jpaEntities.order.PaymentJpaEntity;
import ken.vivid.domain.entities.payment.Payment;
import ken.vivid.domain.entities.payment.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LoadPayment {
    Optional<Payment> loadById(Long id);

    List<Payment> loadByOrderId(Long orderId);

    Optional<PaymentJpaEntity> loadByTransactionReference(String transactionReference);

    /**
     * Sum of the amounts of payments of the given order whose status is in
     * {@code statuses}. Returns {@link BigDecimal#ZERO} (never {@code null})
     * when there is no matching payment.
     */
    BigDecimal sumAmountByOrderIdAndStatusIn(Long orderId, Set<PaymentStatus> statuses);
}
