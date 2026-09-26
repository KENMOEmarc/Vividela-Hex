package ken.vivid.adapter.input.web.output.persistence.jpaRepositories.product;

import ken.vivid.adapter.input.web.output.persistence.jpaEntities.product.StockMovementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementJpaRepository extends JpaRepository<StockMovementJpaEntity, Long> {
}
