package ken.vivid.application.port.output.order;

import ken.vivid.domain.entities.order.Order;
import java.util.List;
import java.util.Optional;

public interface LoadOrder {
    Optional<Order> loadOrderById(Long id);
    List<Order> loadOrderAll();
    List<Order> loadOrderByClientId(Long clientId);
}
