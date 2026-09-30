package ken.vivid.application.service.payment;

import ken.vivid.adapter.exception.DuplicateResourceException;
import ken.vivid.adapter.exception.InvalidRequestException;
import ken.vivid.adapter.exception.InvalidStateTransitionException;
import ken.vivid.adapter.exception.ResourceNotFoundException;
import ken.vivid.adapter.output.persistence.jpaEntities.order.PaymentJpaEntity;
import ken.vivid.application.port.input.payment.recordPayment.RecordPaymentCommand;
import ken.vivid.application.port.output.order.LoadOrder;
import ken.vivid.application.port.output.order.SaveOrder;
import ken.vivid.application.port.output.payment.LoadPayment;
import ken.vivid.application.port.output.payment.SavePayment;
import ken.vivid.domain.entities.order.Order;
import ken.vivid.domain.entities.order.OrderStatus;
import ken.vivid.domain.entities.payment.Payment;
import ken.vivid.domain.entities.payment.PaymentMethodType;
import ken.vivid.domain.entities.payment.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService (application)")
class PaymentServiceTest {

    @Mock
    private LoadPayment loadPayment;
    @Mock
    private SavePayment savePayment;
    @Mock
    private LoadOrder loadOrder;
    @Mock
    private SaveOrder saveOrder;

    @InjectMocks
    private PaymentService paymentService;

    @Nested
    @DisplayName("Record")
    class Record {

        @Test
        @DisplayName("rejects null, zero, or negative amounts before accessing dependencies")
        void recordShouldRejectNonPositiveAmount() {
            assertThatThrownBy(() -> paymentService.record(command(null, "ref-1")))
                    .isInstanceOf(InvalidRequestException.class);
            assertThatThrownBy(() -> paymentService.record(command(BigDecimal.ZERO, "ref-2")))
                    .isInstanceOf(InvalidRequestException.class);
            assertThatThrownBy(() -> paymentService.record(command(new BigDecimal("-1"), "ref-3")))
                    .isInstanceOf(InvalidRequestException.class);

            verifyNoInteractions(loadPayment, savePayment, loadOrder, saveOrder);
        }

        @Test
        @DisplayName("fails when the associated order does not exist")
        void recordShouldThrowWhenOrderNotFound() {
            given(loadOrder.loadOrderById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.record(command(BigDecimal.TEN, "ref-1")))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(savePayment);
        }

        @Test
        @DisplayName("does not accept payments for a cancelled order")
        void recordShouldRejectCancelledOrder() {
            given(loadOrder.loadOrderById(1L))
                    .willReturn(Optional.of(anOrder(1L, OrderStatus.CANCELLED, PaymentStatus.PENDING)));

            assertThatThrownBy(() -> paymentService.record(command(BigDecimal.TEN, "ref-1")))
                    .isInstanceOf(InvalidStateTransitionException.class);

            verifyNoInteractions(savePayment);
        }

        @Test
        @DisplayName("rejects a transaction reference that has already been recorded")
        void recordShouldRejectDuplicateReference() {
            given(loadOrder.loadOrderById(1L))
                    .willReturn(Optional.of(anOrder(1L, OrderStatus.RECEIVED, PaymentStatus.PENDING)));
            given(loadPayment.loadByTransactionReference("duplicate"))
                    .willReturn(Optional.of(PaymentJpaEntity.builder().id(22L).build()));

            assertThatThrownBy(() -> paymentService.record(command(BigDecimal.TEN, "duplicate")))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("22");

            verifyNoInteractions(savePayment);
        }

        @Test
        @DisplayName("prevents committed payments from exceeding the net order balance")
        void recordShouldRejectOverpayment() {
            given(loadOrder.loadOrderById(1L))
                    .willReturn(Optional.of(anOrder(1L, OrderStatus.RECEIVED, PaymentStatus.PENDING)));
            given(loadPayment.loadByTransactionReference("ref-1")).willReturn(Optional.empty());
            given(loadPayment.sumAmountByOrderIdAndStatusIn(1L,
                    EnumSet.of(PaymentStatus.PENDING, PaymentStatus.COMPLETED)))
                    .willReturn(new BigDecimal("70.00"));

            assertThatThrownBy(() -> paymentService.record(command(new BigDecimal("31.00"), "ref-1")))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessageContaining("30.00");

            verifyNoInteractions(savePayment);
        }

        @Test
        @DisplayName("saves an allowed payment in pending status")
        void recordShouldSavePendingPayment() {
            given(loadOrder.loadOrderById(1L))
                    .willReturn(Optional.of(anOrder(1L, OrderStatus.RECEIVED, PaymentStatus.PENDING)));
            given(loadPayment.loadByTransactionReference("ref-1")).willReturn(Optional.empty());
            given(loadPayment.sumAmountByOrderIdAndStatusIn(1L,
                    EnumSet.of(PaymentStatus.PENDING, PaymentStatus.COMPLETED)))
                    .willReturn(new BigDecimal("25.00"));
            given(savePayment.save(any(Payment.class))).willAnswer(invocation -> invocation.getArgument(0));

            Payment saved = paymentService.record(command(new BigDecimal("25.00"), "ref-1"));

            ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
            verify(savePayment).save(captor.capture());
            assertThat(saved.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(saved.getOrderId()).isEqualTo(1L);
            assertThat(saved.getAmount()).isEqualByComparingTo("25.00");
            assertThat(saved.getTransactionReference()).isEqualTo("ref-1");
            assertThat(captor.getValue()).isSameAs(saved);
        }
    }

    @Nested
    @DisplayName("Confirm")
    class Confirm {

        @Test
        @DisplayName("completes a pending payment and reconciles a fully paid order")
        void confirmShouldCompletePaymentAndOrder() {
            Payment payment = aPayment(10L, 1L, PaymentStatus.PENDING);
            Order order = anOrder(1L, OrderStatus.RECEIVED, PaymentStatus.PENDING);
            given(loadPayment.loadById(10L)).willReturn(Optional.of(payment));
            given(savePayment.save(payment)).willReturn(payment);
            given(loadOrder.loadOrderById(1L)).willReturn(Optional.of(order));
            given(loadPayment.sumAmountByOrderIdAndStatusIn(1L, EnumSet.of(PaymentStatus.COMPLETED)))
                    .willReturn(new BigDecimal("100.00"));

            Payment confirmed = paymentService.confirm(10L, 8L);

            assertThat(confirmed.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
            assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
            verify(savePayment).save(payment);
            verify(saveOrder).save(order);
        }

        @Test
        @DisplayName("does not overwrite a manually refunded order status")
        void confirmShouldPreserveRefundedOrderStatus() {
            Payment payment = aPayment(10L, 1L, PaymentStatus.PENDING);
            Order order = anOrder(1L, OrderStatus.RECEIVED, PaymentStatus.REFUNDED);
            given(loadPayment.loadById(10L)).willReturn(Optional.of(payment));
            given(savePayment.save(payment)).willReturn(payment);
            given(loadOrder.loadOrderById(1L)).willReturn(Optional.of(order));

            paymentService.confirm(10L, 8L);

            assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.REFUNDED);
            verify(loadPayment, never()).sumAmountByOrderIdAndStatusIn(any(), any());
            verifyNoInteractions(saveOrder);
        }

        @Test
        @DisplayName("rejects confirming a payment that is no longer pending")
        void confirmShouldRejectNonPendingPayment() {
            given(loadPayment.loadById(10L))
                    .willReturn(Optional.of(aPayment(10L, 1L, PaymentStatus.COMPLETED)));

            assertThatThrownBy(() -> paymentService.confirm(10L, 8L))
                    .isInstanceOf(InvalidStateTransitionException.class);

            verifyNoInteractions(savePayment, loadOrder, saveOrder);
        }
    }

    @Nested
    @DisplayName("Fail and Read")
    class FailAndRead {

        @Test
        @DisplayName("marks a pending payment failed and persists it")
        void failShouldMarkPaymentFailed() {
            Payment payment = aPayment(10L, 1L, PaymentStatus.PENDING);
            given(loadPayment.loadById(10L)).willReturn(Optional.of(payment));
            given(savePayment.save(payment)).willReturn(payment);

            assertThat(paymentService.fail(10L, 8L).getStatus()).isEqualTo(PaymentStatus.FAILED);

            verify(savePayment).save(payment);
            verifyNoInteractions(loadOrder, saveOrder);
        }

        @Test
        @DisplayName("fails when a payment lookup cannot find the requested payment")
        void getByIdShouldThrowWhenPaymentNotFound() {
            given(loadPayment.loadById(404L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.getById(404L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("404");
        }

        @Test
        @DisplayName("returns payments for the requested order")
        void getByOrderIdShouldReturnPortResults() {
            List<Payment> payments = List.of(aPayment(10L, 1L, PaymentStatus.PENDING));
            given(loadPayment.loadByOrderId(1L)).willReturn(payments);

            assertThat(paymentService.getByOrderId(1L)).containsExactlyElementsOf(payments);
        }
    }

    private RecordPaymentCommand command(BigDecimal amount, String transactionReference) {
        return new RecordPaymentCommand(1L, PaymentMethodType.CASH, amount,
                "555-0100", transactionReference, 8L);
    }

    private Order anOrder(Long id, OrderStatus status, PaymentStatus paymentStatus) {
        Instant now = Instant.now();
        return Order.create(id, 7L, LocalDate.now(), null, null, status, paymentStatus,
                null, new BigDecimal("100.00"), BigDecimal.ZERO, now, now);
    }

    private Payment aPayment(Long id, Long orderId, PaymentStatus status) {
        Instant now = Instant.now();
        return Payment.create(id, orderId, PaymentMethodType.CASH, new BigDecimal("100.00"),
                "555-0100", "ref-" + id, status, now, 8L, now, now);
    }
}
