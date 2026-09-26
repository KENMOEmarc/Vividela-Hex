package ken.vivid.application.port.input.order.createOrder;

import ken.vivid.domain.entities.order.Order;

public interface CreateOrderUseCase {
    Order create(CreateOrderCommand command);
}
