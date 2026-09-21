package ken.vivid.application.port.output.order;

import ken.vivid.domain.entities.order.Order;

public interface SaveOrder {
    Order save(Order order);
}
