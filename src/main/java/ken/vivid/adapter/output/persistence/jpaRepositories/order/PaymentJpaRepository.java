package ken.vivid.adapter.output.persistence.jpaRepositories.order;

import ken.vivid.adapter.output.persistence.jpaEntities.order.PaymentJpaEntity;
import ken.vivid.domain.entities.payment.Payment;
import ken.vivid.domain.entities.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {

    Set<PaymentJpaEntity> findByOrderId(Long orderId);

    Optional<PaymentJpaEntity> findByTransactionReference(String transactionReference);

    @Query("SELECT SUM(p.amount) FROM PaymentJpaEntity p WHERE p.orderId = :orderId AND p.status IN :statuses")
    Optional<BigDecimal> sumAmountByOrderIdAndStatusIn(@Param("orderId") Long orderId, @Param("statuses") Set<PaymentStatus> statuses);}
