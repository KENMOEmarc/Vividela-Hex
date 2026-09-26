package ken.vivid.adapter.input.web.output.persistence.jpaRepositories.order;

import ken.vivid.adapter.input.web.output.persistence.jpaEntities.order.OrderJpaEntity;
import ken.vivid.domain.entities.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {
    List<OrderJpaEntity> findByClientIdOrderByOrderDateDesc(Long clientId);

    List<Order> findByClientId(Long clientId);
}
