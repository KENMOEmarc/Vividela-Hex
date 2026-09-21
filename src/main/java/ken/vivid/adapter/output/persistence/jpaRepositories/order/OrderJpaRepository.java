package ken.vivid.adapter.output.persistence.jpaRepositories.order;

import ken.vivid.adapter.output.persistence.jpaEntities.order.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {
    List<OrderJpaEntity> findByClientIdOrderByOrderDateDesc(Long clientId);
}
