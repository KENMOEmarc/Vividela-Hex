package ken.vivid.adapter.input.web.output.persistence.adapter.order;

import ken.vivid.adapter.input.web.output.persistence.jpaEntities.order.PaymentJpaEntity;
import ken.vivid.adapter.input.web.output.persistence.jpaRepositories.order.OrderJpaRepository;
import ken.vivid.adapter.input.web.output.persistence.jpaRepositories.order.PaymentJpaRepository;
import ken.vivid.application.port.output.order.LoadOrder;
import ken.vivid.application.port.output.order.SaveOrder;
import ken.vivid.application.port.output.payment.LoadPayment;
import ken.vivid.application.port.output.payment.SavePayment;
import ken.vivid.domain.entities.order.Order;
import ken.vivid.domain.entities.payment.Payment;
import ken.vivid.domain.entities.payment.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PaymentPersistenceAdapter implements LoadPayment, SavePayment, LoadOrder, SaveOrder {
    private final PaymentJpaRepository paymentRepository;
    private final OrderJpaRepository orderRepository;

    public PaymentPersistenceAdapter(PaymentJpaRepository paymentRepository, OrderJpaRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Optional<Payment> loadById(Long id) {
        return paymentRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Payment> loadByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .stream().map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Payment> loadByTransactionReference(String transactionReference) {
        return paymentRepository.findByTransactionReference(transactionReference);
    }

    @Override
    public BigDecimal sumAmountByOrderIdAndStatusIn(Long orderId, Set<PaymentStatus> statuses) {
        return paymentRepository.sumAmountByOrderIdAndStatusIn(orderId, statuses)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public Payment save(Payment payment) {
        return toDomain(paymentRepository.save(toEntity(payment)));
    }

    private Payment toDomain(PaymentJpaEntity entity) {
        return Payment.create(entity.getId(), entity.getOrderId(), entity.getPaymentMethodType(), entity.getAmount(),
                entity.getPayerPhone(), entity.getTransactionReference(), entity.getStatus(),
                entity.getPaymentDate(), entity.getCreatedBy(), entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private PaymentJpaEntity toEntity(Payment payment) {
        return PaymentJpaEntity.builder().id(payment.getId()).orderId(payment.getOrderId())
                .amount(payment.getAmount()).paymentMethodType(payment.getPaymentMethod())
                .paymentDate(payment.getPaidAt()).status(payment.getStatus())
                .createdAt(payment.getCreatedAt()).updatedAt(payment.getUpdatedAt()).build();
    }

    @Override
    public Optional<Order> loadOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderPersistenceAdapter::toDomain);
    }

    @Override
    public List<Order> loadOrderAll() {
        return orderRepository.findAll().stream().map(OrderPersistenceAdapter::toDomain).toList();
    }

    @Override
    public List<Order> loadOrderByClientId(Long clientId) {
        return orderRepository.findByClientId(clientId);
    }

    @Override
    public Order save(Order order) {
        return OrderPersistenceAdapter.toDomain(orderRepository
                .save(OrderPersistenceAdapter.toEntity(order))
        );
    }
}