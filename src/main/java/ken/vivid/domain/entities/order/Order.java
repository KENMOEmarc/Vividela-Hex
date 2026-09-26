package ken.vivid.domain.entities.order;

import ken.vivid.domain.entities.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class Order {
    private Long id;
    private Long clientId;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private LocalDate deliveredAt;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String notes;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private Instant createdAt;
    private Instant updatedAt;

    private Order(Long id, Long clientId, LocalDate orderDate, LocalDate expectedDeliveryDate,
                  LocalDate deliveredAt, OrderStatus status, PaymentStatus paymentStatus,
                  String notes, BigDecimal totalAmount, BigDecimal discountAmount,
                  Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.clientId = clientId;
        this.orderDate = orderDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.deliveredAt = deliveredAt;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.notes = notes;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Order create(Long id, Long clientId, LocalDate orderDate,
                               LocalDate expectedDeliveryDate, LocalDate deliveredAt,
                               OrderStatus status, PaymentStatus paymentStatus, String notes,
                               BigDecimal totalAmount, BigDecimal discountAmount,
                               Instant createdAt, Instant updatedAt) {
        if (clientId == null || orderDate == null || status == null || paymentStatus == null
                || totalAmount == null || discountAmount == null || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("Required order fields cannot be null");
        }
        if (expectedDeliveryDate != null && expectedDeliveryDate.isBefore(orderDate)
                || deliveredAt != null && deliveredAt.isBefore(orderDate)
                || totalAmount.signum() < 0 || discountAmount.signum() < 0
                || discountAmount.compareTo(totalAmount) > 0) {
            throw new IllegalArgumentException("Invalid order values");
        }
        return new Order(id, clientId, orderDate, expectedDeliveryDate, deliveredAt, status,
                paymentStatus, notes, totalAmount, discountAmount, createdAt, updatedAt);
    }

    public Long getId() { return id; }
    public Long getClientId() { return clientId; }
    public LocalDate getOrderDate() { return orderDate; }
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public LocalDate getDeliveredAt() { return deliveredAt; }
    public OrderStatus getStatus() { return status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public String getNotes() { return notes; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(LocalDate expectedDeliveryDate, OrderStatus status,
                       PaymentStatus paymentStatus, String notes,
                       BigDecimal totalAmount, BigDecimal discountAmount, Instant updatedAt) {
        if (status == null || paymentStatus == null || totalAmount == null || discountAmount == null
                || totalAmount.signum() < 0 || discountAmount.signum() < 0
                || discountAmount.compareTo(totalAmount) > 0
                || expectedDeliveryDate != null && expectedDeliveryDate.isBefore(orderDate)) {
            throw new IllegalArgumentException("Invalid order update");
        }
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.notes = notes;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        if (status == OrderStatus.DELIVERED && deliveredAt == null) {
            deliveredAt = LocalDate.now();
        }
        this.updatedAt = updatedAt;
    }

    public void updatePaymentStatus(PaymentStatus newStatus, Instant now) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Payment status cannot be null");
        }
        this.paymentStatus = newStatus;
        this.updatedAt = now;
    }

    public BigDecimal getNetAmountDue() {
        return getTotalAmount().subtract(getDiscountAmount());
    }
}
