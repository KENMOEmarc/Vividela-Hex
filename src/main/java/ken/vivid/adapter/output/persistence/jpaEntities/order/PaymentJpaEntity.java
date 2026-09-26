package ken.vivid.adapter.output.persistence.jpaEntities.order;

import jakarta.persistence.*;
import ken.vivid.domain.entities.payment.PaymentMethodType;
import ken.vivid.domain.entities.payment.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_method_type")
    private PaymentMethodType paymentMethodType;

    @Column(name = "payment_date")
    private Instant paymentDate;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payer_phone")
    private String payerPhone;

    @Column(name = "transaction_reference")
    private String transactionReference;

    @Column(name = "status")
    private PaymentStatus status;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    private  void beforeInsert() {
        Instant now = Instant.now();
        this.paidAt = now;
    }
}
