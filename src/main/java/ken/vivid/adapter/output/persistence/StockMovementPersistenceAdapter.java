package ken.vivid.adapter.output.persistence;

import ken.vivid.adapter.output.persistence.entities.StockMovementJpaEntity;
import ken.vivid.adapter.output.persistence.repositories.StockMovementJpaRepository;
import ken.vivid.domain.port.output.SaveStockMovement;
import ken.vivid.domain.entities.StockMovement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockMovementPersistenceAdapter implements SaveStockMovement {

    private final StockMovementJpaRepository jpaRepository;

    @Override
    public StockMovement save(StockMovement movement) {
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
