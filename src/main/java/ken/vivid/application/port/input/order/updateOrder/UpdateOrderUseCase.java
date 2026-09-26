package ken.vivid.application.port.input.order.updateOrder;

import ken.vivid.domain.entities.order.Order;

public interface UpdateOrderUseCase {
    Order update(UpdateOrderCommand command);
}
