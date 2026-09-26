package ken.vivid.domain.entities.payment;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A single payment recorded against an {@code Order}. An order can receive
 * several payments (partial payments); the order's own {@code paymentStatus}
 * is reconciled from the sum of its COMPLETED payments (see
 * {@code ken.vivid.application.service.payment.PaymentService}).
 */
public class Payment {
    private Long id;
    private Long orderId;
    private PaymentMethodType paymentMethod;
    private BigDecimal amount;
    private String payerPhone;
    private String transactionReference;
    private PaymentStatus status;
    private Instant paidAt;
    private Long createdBy;
    private  Instant createdAt;
    private  Instant updatedAt;

    private Payment(Long id, Long orderId, PaymentMethodType paymentMethod, BigDecimal amount,
                    String payerPhone, String transactionReference, PaymentStatus status,
                    Instant paidAt, Long createdBy, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.payerPhone = payerPhone;
        this.transactionReference = transactionReference;
        this.status = status;
        this.paidAt = paidAt;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Payment create(Long id, Long orderId, PaymentMethodType paymentMethod, BigDecimal amount,
                                 String payerPhone, String transactionReference, PaymentStatus status,
                                 Instant paidAt, Long createdBy, Instant createdAt, Instant updatedAt) {
        if (orderId == null || paymentMethod == null || amount == null || status == null) {
            throw new IllegalArgumentException("Required payment fields cannot be null");
        }
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Payment amount must be strictly positive");
        }
        return new Payment(id, orderId, paymentMethod, amount, payerPhone, transactionReference,
                status, paidAt, createdBy, createdAt, updatedAt);
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public PaymentMethodType getPaymentMethod() { return paymentMethod; }
    public BigDecimal getAmount() { return amount; }
    public String getPayerPhone() { return payerPhone; }
    public String getTransactionReference() { return transactionReference; }
    public PaymentStatus getStatus() { return status; }
    public Instant getPaidAt() { return paidAt; }
    public Long getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Marks this payment as effectively collected. Transition legality
     * (only a PENDING payment can be confirmed) is enforced by the
     * application service, which is where {@code InvalidStateTransitionException}
     * is thrown.
     */
    public void markCompleted() {
        this.status = PaymentStatus.COMPLETED;
    }

    /** Marks this payment as failed (e.g. bounced check, cancelled mobile payment). */
    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }
}
