package ken.vivid.adapter.output.persistence;

import ken.vivid.adapter.output.persistence.entities.StockJpaEntity;
import ken.vivid.adapter.output.persistence.repositories.StockJpaRepository;
import ken.vivid.domain.port.output.LoadStock;
import ken.vivid.domain.port.output.SaveStock;
import ken.vivid.domain.entities.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StockPersistenceAdapter implements LoadStock, SaveStock {

    private final StockJpaRepository jpaRepository;

    @Override
    public Optional<Stock> loadById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Stock> loadAvailableByProductOrderedByExpiration(Long productId) {
        return jpaRepository
                .findByProductIdAndCurrentQuantityGreaterThanOrderByExpirationDateAsc(productId, BigDecimal.ZERO)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public BigDecimal totalQuantityByProduct(Long productId) {
        return jpaRepository.sumCurrentQuantityByProductId(productId);
    }

    @Override
    public Stock save(Stock stock) {
        StockJpaEntity saved = jpaRepository.save(toEntity(stock));
        return toDomain(saved);
    }

    private Stock toDomain(StockJpaEntity entity) {
        return Stock.createStock(
                entity.getId(),
                entity.getProductId(),
                entity.getCurrentQuantity(),
                entity.getUpdatedAt(),
                toInstant(entity.getEntryDate()),
                entity.getUnitPrice(),
                toInstant(entity.getExpirationDate())
        );
    }

    private StockJpaEntity toEntity(Stock stock) {
        return StockJpaEntity.builder()
                .id(stock.getId())
                .productId(stock.getProductId())
                .currentQuantity(stock.getQuantity())
                .unitPrice(stock.getUnitPrice())
                .entryDate(toLocalDate(stock.getEntryDate()))
                .expirationDate(toLocalDate(stock.getExpirationDate()))
                .updatedAt(stock.getUpdatedAt())
                .build();
    }

    private Instant toInstant(LocalDate date) {
        return date == null ? null : date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    private LocalDate toLocalDate(Instant instant) {
        return instant == null ? null : instant.atZone(ZoneOffset.UTC).toLocalDate();
    }
}
