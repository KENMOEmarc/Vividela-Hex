package ken.vivid.application.port.output.order;

import ken.vivid.domain.entities.order.Order;
import java.util.List;
import java.util.Optional;

public interface LoadOrder {
    Optional<Order> loadById(Long id);
    List<Order> loadAll();
    List<Order> loadByClientId(Long clientId);
}
