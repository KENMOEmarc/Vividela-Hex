package ken.vivid.application.port.input.order;

import ken.vivid.domain.entities.order.Order;
import java.util.List;

public interface GetOrderUseCase {
    Order getById(Long id);
    List<Order> getAll();
    List<Order> getByClientId(Long clientId);
}
