package ken.vivid.application.service.order;

import ken.vivid.adapter.exception.InvalidStateTransitionException;
import ken.vivid.adapter.exception.ResourceNotFoundException;
import ken.vivid.application.port.input.order.createOrder.CreateOrderCommand;
import ken.vivid.application.port.input.order.updateOrder.UpdateOrderCommand;
import ken.vivid.application.port.output.order.DeleteOrder;
import ken.vivid.application.port.output.order.LoadOrder;
import ken.vivid.application.port.output.order.SaveOrder;
import ken.vivid.domain.entities.order.Order;
import ken.vivid.domain.entities.order.OrderStatus;
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
@DisplayName("OrderService (application)")
class OrderServiceTest {

    @Mock
    private LoadOrder loadOrder;
    @Mock
    private SaveOrder saveOrder;
    @Mock
    private DeleteOrder deleteOrder;

    @InjectMocks
    private OrderService orderService;

    @Nested
    @DisplayName("Create")
    class Create {

        @Test
        @DisplayName("creates a received order with pending payment and zero defaults for missing amounts")
        void createShouldSaveNewOrderWithDefaults() {
            given(saveOrder.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

            Order created = orderService.create(new CreateOrderCommand(
                    7L, LocalDate.now().plusDays(3), "Rush", null, null));

            ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            verify(saveOrder).save(captor.capture());
            Order saved = captor.getValue();
            assertThat(created).isSameAs(saved);
            assertThat(saved.getClientId()).isEqualTo(7L);
            assertThat(saved.getOrderDate()).isEqualTo(LocalDate.now());
            assertThat(saved.getExpectedDeliveryDate()).isEqualTo(LocalDate.now().plusDays(3));
            assertThat(saved.getStatus()).isEqualTo(OrderStatus.RECEIVED);
            assertThat(saved.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(saved.getNotes()).isEqualTo("Rush");
            assertThat(saved.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(saved.getDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Read")
    class Read {

        @Test
        @DisplayName("returns the requested order")
        void getByIdShouldReturnOrder() {
            Order order = anOrder(1L, OrderStatus.RECEIVED, PaymentStatus.PENDING);
            given(loadOrder.loadOrderById(1L)).willReturn(Optional.of(order));

            assertThat(orderService.getById(1L)).isSameAs(order);
        }

        @Test
        @DisplayName("fails when the requested order does not exist")
        void getByIdShouldThrowWhenOrderNotFound() {
            given(loadOrder.loadOrderById(404L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.getById(404L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("404");
        }

        @Test
        @DisplayName("delegates all-order and client-order queries to the load port")
        void queriesShouldReturnPortResults() {
            Order first = anOrder(1L, OrderStatus.RECEIVED, PaymentStatus.PENDING);
            Order second = anOrder(2L, OrderStatus.READY, PaymentStatus.COMPLETED);
            given(loadOrder.loadOrderAll()).willReturn(List.of(first, second));
            given(loadOrder.loadOrderByClientId(7L)).willReturn(List.of(first));

            assertThat(orderService.getAll()).containsExactly(first, second);
            assertThat(orderService.getByClientId(7L)).containsExactly(first);
        }
    }

    @Nested
    @DisplayName("Update")
    class Update {

        @Test
        @DisplayName("applies permitted order changes and persists the order")
        void updateShouldChangeAndSaveOrder() {
            Order order = anOrder(1L, OrderStatus.IN_PROGRESS, PaymentStatus.PENDING);
            given(loadOrder.loadOrderById(1L)).willReturn(Optional.of(order));
            given(saveOrder.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

            Order updated = orderService.update(new UpdateOrderCommand(
                    1L, LocalDate.now().plusDays(5), OrderStatus.READY, PaymentStatus.COMPLETED,
                    "Ready for pickup", new BigDecimal("125.00"), new BigDecimal("10.00")));

            assertThat(updated).isSameAs(order);
            assertThat(updated.getStatus()).isEqualTo(OrderStatus.READY);
            assertThat(updated.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
            assertThat(updated.getNotes()).isEqualTo("Ready for pickup");
            assertThat(updated.getTotalAmount()).isEqualByComparingTo("125.00");
            assertThat(updated.getDiscountAmount()).isEqualByComparingTo("10.00");
            verify(saveOrder).save(order);
        }

        @Test
        @DisplayName("rejects a transition out of a terminal status without saving")
        void updateShouldRejectTransitionFromDeliveredOrder() {
            given(loadOrder.loadOrderById(1L))
                    .willReturn(Optional.of(anOrder(1L, OrderStatus.DELIVERED, PaymentStatus.COMPLETED)));

            assertThatThrownBy(() -> orderService.update(new UpdateOrderCommand(
                    1L, null, OrderStatus.IN_PROGRESS, PaymentStatus.PENDING, null,
                    BigDecimal.TEN, BigDecimal.ZERO)))
                    .isInstanceOf(InvalidStateTransitionException.class);

            verifyNoInteractions(saveOrder);
        }

        @Test
        @DisplayName("fails when the order to update does not exist")
        void updateShouldThrowWhenOrderNotFound() {
            given(loadOrder.loadOrderById(404L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.update(new UpdateOrderCommand(
                    404L, null, OrderStatus.READY, PaymentStatus.PENDING, null,
                    BigDecimal.TEN, BigDecimal.ZERO)))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(saveOrder);
        }
    }

    @Nested
    @DisplayName("Delete")
    class Delete {

        @Test
        @DisplayName("delegates deletion of the requested order")
        void deleteShouldDelegateToPort() {
            orderService.delete(9L);

            verify(deleteOrder).delete(9L);
            verify(loadOrder, never()).loadOrderById(any());
        }
    }

    private Order anOrder(Long id, OrderStatus status, PaymentStatus paymentStatus) {
        Instant now = Instant.now();
        return Order.create(id, 7L, LocalDate.now(), null, null, status, paymentStatus,
                null, new BigDecimal("100.00"), BigDecimal.ZERO, now, now);
    }
}
