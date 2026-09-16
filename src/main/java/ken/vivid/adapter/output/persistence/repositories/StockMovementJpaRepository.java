package ken.vivid.adapter.output.persistence.repositories;

import ken.vivid.adapter.output.persistence.entities.StockMovementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementJpaRepository extends JpaRepository<StockMovementJpaEntity, Long> {
}
