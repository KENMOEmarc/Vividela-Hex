package ken.vivid.adapter.input.web.output.persistence.jpaRepositories.order;

import ken.vivid.adapter.input.web.output.persistence.jpaEntities.order.PaymentJpaEntity;
import ken.vivid.domain.entities.payment.Payment;
import ken.vivid.domain.entities.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {

    Set<PaymentJpaEntity> findByOrderId(Long orderId);

    Optional<Payment> findByTransactionReference(String transactionReference);

    Optional<BigDecimal> sumAmountByOrderIdAndStatusIn(Long orderId, Set<PaymentStatus> statuses);
}
