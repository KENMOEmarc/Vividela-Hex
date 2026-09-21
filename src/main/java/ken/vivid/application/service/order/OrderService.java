package ken.vivid.application.service.order;

import ken.vivid.adapter.exception.InvalidStateTransitionException;
import ken.vivid.adapter.exception.ResourceNotFoundException;
import ken.vivid.application.port.input.order.*;
import ken.vivid.application.port.output.order.DeleteOrder;
import ken.vivid.application.port.output.order.LoadOrder;
import ken.vivid.application.port.output.order.SaveOrder;
import ken.vivid.domain.entities.order.Order;
import ken.vivid.domain.entities.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class OrderService implements CreateOrderUseCase, GetOrderUseCase,
        UpdateOrderUseCase, DeleteOrderUseCase {
    private final LoadOrder loadOrder;
    private final SaveOrder saveOrder;
    private final DeleteOrder deleteOrder;

    public OrderService(LoadOrder loadOrder, SaveOrder saveOrder, DeleteOrder deleteOrder) {
        this.loadOrder = loadOrder;
        this.saveOrder = saveOrder;
        this.deleteOrder = deleteOrder;
    }

    @Override
    public Order create(CreateOrderCommand command) {
        Instant now = Instant.now();
        return saveOrder.save(Order.create(null, command.clientId(), LocalDate.now(),
                command.expectedDeliveryDate(), null, OrderStatus.RECEIVED,
                ken.vivid.domain.entities.order.PaymentStatus.PENDING, command.notes(),
                amount(command.totalAmount()), amount(command.discountAmount()), now, now));
    }

    @Override
    public Order getById(Long id) {
        return loadOrder.loadById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found : " + id));
    }

    @Override
    public List<Order> getAll() { return loadOrder.loadAll(); }

    @Override
    public List<Order> getByClientId(Long clientId) { return loadOrder.loadByClientId(clientId); }

    @Override
    public Order update(UpdateOrderCommand command) {
        Order target = getById(command.orderId());
        validateTransition(target.getStatus(), command.status());
        target.update(command.expectedDeliveryDate(), command.status(), command.paymentStatus(),
                command.notes(), command.totalAmount(), command.discountAmount(), Instant.now());
        return saveOrder.save(target);
    }

    @Override
    public void delete(Long id) {
        getById(id);
        deleteOrder.delete(id);
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        if (next == null || current == OrderStatus.DELIVERED && next != OrderStatus.DELIVERED
                || current == OrderStatus.CANCELLED && next != OrderStatus.CANCELLED) {
            throw new InvalidStateTransitionException("Invalid order status transition: " + current + " -> " + next);
        }
    }

    private BigDecimal amount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
