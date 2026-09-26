package ken.vivid.application.service.payment;

import ken.vivid.adapter.exception.DuplicateResourceException;
import ken.vivid.adapter.exception.InvalidRequestException;
import ken.vivid.adapter.exception.InvalidStateTransitionException;
import ken.vivid.adapter.exception.ResourceNotFoundException;
import ken.vivid.application.port.input.payment.*;
import ken.vivid.application.port.input.payment.recordPayment.RecordPaymentCommand;
import ken.vivid.application.port.input.payment.recordPayment.RecordPaymentUseCase;
import ken.vivid.application.port.output.order.LoadOrder;
import ken.vivid.application.port.output.order.SaveOrder;
import ken.vivid.application.port.output.payment.LoadPayment;
import ken.vivid.application.port.output.payment.SavePayment;
import ken.vivid.domain.entities.order.Order;
import ken.vivid.domain.entities.order.OrderStatus;
import ken.vivid.domain.entities.payment.Payment;
import ken.vivid.domain.entities.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Application service for the Payment module.
 * <p>
 * Business rules (ported from the layered reference implementation):
 * <ul>
 *     <li>a cancelled order can no longer receive payments;</li>
 *     <li>the payment amount must be strictly positive;</li>
 *     <li>a transaction reference cannot be recorded twice (double click /
 *     replayed webhook guard);</li>
 *     <li>the sum of "committed" payments (PENDING + COMPLETED) of an order
 *     can never exceed its net amount due (overpayment guard);</li>
 *     <li>a payment is always created as PENDING; only {@link #confirm} or
 *     {@link #fail} can move it out of that state, and only once;</li>
 *     <li>confirming a payment reconciles {@code Order.paymentStatus} from
 *     the sum of its COMPLETED payments, without ever overriding a manual
 *     REFUNDED status.</li>
 * </ul>
 */
public class PaymentService implements RecordPaymentUseCase, ConfirmPaymentUseCase,
        FailPaymentUseCase, GetPaymentUseCase {

    private static final Set<PaymentStatus> COMMITTED_STATUSES = EnumSet.of(PaymentStatus.PENDING, PaymentStatus.COMPLETED);
    private static final Set<PaymentStatus> COMPLETED_STATUS = EnumSet.of(PaymentStatus.COMPLETED);

    private final LoadPayment loadPayment;
    private final SavePayment savePayment;
    private final LoadOrder loadOrder;
    private final SaveOrder saveOrder;

    public PaymentService(LoadPayment loadPayment, SavePayment savePayment, LoadOrder loadOrder, SaveOrder saveOrder) {
        this.loadPayment = loadPayment;
        this.savePayment = savePayment;
        this.loadOrder = loadOrder;
        this.saveOrder = saveOrder;
    }

    @Override
    public Payment record(RecordPaymentCommand command) {
        if (command.amount() == null || command.amount().signum() <= 0) {
            throw new InvalidRequestException("Payment amount must be strictly positive");
        }

        Order order = loadOrder.loadOrderById(command.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found : " + command.orderId()));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidStateTransitionException(
                    "Cannot record a payment: order " + order.getId() + " is cancelled");
        }

        if (command.transactionReference() != null && !command.transactionReference().isBlank()) {
            loadPayment.loadByTransactionReference(command.transactionReference())
                    .ifPresent(existing -> {
                        throw new DuplicateResourceException(
                                "A payment with transaction reference '" + command.transactionReference()
                                        + "' has already been recorded (payment #" + existing.getId() + ")");
                    });
        }

        BigDecimal netAmountDue = order.getNetAmountDue();
        BigDecimal alreadyCommitted = loadPayment.sumAmountByOrderIdAndStatusIn(order.getId(), COMMITTED_STATUSES);
        BigDecimal newTotal = alreadyCommitted.add(command.amount());
        if (newTotal.compareTo(netAmountDue) > 0) {
            BigDecimal remaining = netAmountDue.subtract(alreadyCommitted);
            throw new InvalidRequestException(
                    "This payment of " + command.amount() + " exceeds the remaining balance of order "
                            + order.getId() + " (" + (remaining.signum() > 0 ? remaining : BigDecimal.ZERO)
                            + " remaining out of a net total of " + netAmountDue + ")");
        }

        Payment payment = Payment.create(order.getId(), command.orderId(),command.paymentMethod(), command.amount(),
                command.payerPhone(), command.transactionReference(), PaymentStatus.PENDING,
                Instant.now(), command.actingUserId(), Instant.now(), Instant.now());

        return savePayment.save(payment);
    }

    @Override
    public Payment confirm(Long paymentId, Long actingUserId) {
        Payment payment = getById(paymentId);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidStateTransitionException(
                    "Only a PENDING payment can be confirmed (current status: " + payment.getStatus() + ")");
        }

        payment.markCompleted();
        Payment saved = savePayment.save(payment);

        reconcileOrderPaymentStatus(payment.getOrderId());

        return saved;
    }

    @Override
    public Payment fail(Long paymentId, Long actingUserId) {
        Payment payment = getById(paymentId);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidStateTransitionException(
                    "Only a PENDING payment can be marked as failed (current status: " + payment.getStatus() + ")");
        }

        payment.markFailed();
        return savePayment.save(payment);
    }

    @Override
    public Payment getById(Long id) {
        return loadPayment.loadById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found : " + id));
    }

    @Override
    public List<Payment> getByOrderId(Long orderId) {
        return loadPayment.loadByOrderId(orderId);
    }

    /**
     * Recomputes {@code Order.paymentStatus} from the sum of its COMPLETED
     * payments compared to its net amount due:
     * <ul>
     *     <li>completed total &gt;= net amount due (and net amount due &gt; 0) → COMPLETED;</li>
     *     <li>otherwise (partial or no confirmed payment) → PENDING.</li>
     * </ul>
     * Never touches an order already manually set to REFUNDED (e.g. a refund
     * handled by staff outside the payment collection flow).
     */
    private void reconcileOrderPaymentStatus(Long orderId) {
        Order order = loadOrder.loadOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found : " + orderId));

        if (order.getPaymentStatus() == PaymentStatus.REFUNDED) {
            return;
        }

        BigDecimal netAmountDue = order.getNetAmountDue();
        BigDecimal completedTotal = loadPayment.sumAmountByOrderIdAndStatusIn(orderId, COMPLETED_STATUS);

        PaymentStatus newStatus = completedTotal.compareTo(netAmountDue) >= 0 && netAmountDue.signum() > 0
                ? PaymentStatus.COMPLETED
                : PaymentStatus.PENDING;

        if (newStatus != order.getPaymentStatus()) {
            order.updatePaymentStatus(newStatus, Instant.now());
            saveOrder.save(order);
        }
    }

}
