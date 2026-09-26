package ken.vivid.adapter.output.persistence.jpaRepositories.product;

import ken.vivid.adapter.output.persistence.jpaEntities.product.StockMovementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementJpaRepository extends JpaRepository<StockMovementJpaEntity, Long> {
}
