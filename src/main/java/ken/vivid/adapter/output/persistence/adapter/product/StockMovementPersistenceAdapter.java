package ken.vivid.adapter.output.persistence.adapter.product;

import ken.vivid.adapter.output.persistence.jpaEntities.product.StockMovementJpaEntity;
import ken.vivid.adapter.output.persistence.jpaRepositories.product.StockMovementJpaRepository;
import ken.vivid.application.port.output.product.stock.movement.SaveStockMovement;
import ken.vivid.domain.entities.StockMovement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockMovementPersistenceAdapter implements SaveStockMovement {

    private final StockMovementJpaRepository jpaRepository;

    @Override
    public StockMovement save(StockMovement movement) {
        log.info("Persisting stock movement for stockId={} type={} quantity={}",
                movement.getStockId(), movement.getMovementType(), movement.getQuantity());
        StockMovementJpaEntity saved = jpaRepository.save(toEntity(movement));
        return toDomain(saved);
    }

    private StockMovement toDomain(StockMovementJpaEntity entity) {
        return StockMovement.createStockMovement(
                entity.getId(),
                entity.getStockId(),
                entity.getUserId(),
                entity.getQuantity(),
                entity.getMovementType(),
                entity.getNotes(),
                entity.getMovementDate()
        );
    }

    private StockMovementJpaEntity toEntity(StockMovement movement) {
        return StockMovementJpaEntity.builder()
                .id(movement.getId())
                .stockId(movement.getStockId())
                .userId(movement.getUserId())
                .quantity(movement.getQuantity())
                .movementType(movement.getMovementType())
                .notes(movement.getNotes())
                .movementDate(movement.getMovementDate())
                .build();
    }
}
