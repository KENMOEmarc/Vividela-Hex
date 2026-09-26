package ken.vivid.adapter.input.web.output.persistence.adapter.product;

import ken.vivid.adapter.input.web.output.persistence.jpaEntities.product.StockJpaEntity;
import ken.vivid.adapter.input.web.output.persistence.jpaRepositories.product.StockJpaRepository;
import ken.vivid.application.port.output.product.stock.LoadStock;
import ken.vivid.application.port.output.product.stock.SaveStock;
import ken.vivid.domain.entities.product.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockPersistenceAdapter implements LoadStock, SaveStock {

    private final StockJpaRepository jpaRepository;

    @Override
    public Optional<Stock> loadById(Long id) {
        log.debug("Loading stock batch by id={}", id);
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Stock> loadAvailableByProductOrderedByExpiration(Long productId) {
        log.debug("Loading available stock batches for productId={}", productId);
        return jpaRepository
                .findByProductIdAndCurrentQuantityGreaterThanOrderByExpirationDateAsc(productId, BigDecimal.ZERO)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public BigDecimal totalQuantityByProduct(Long productId) {
        BigDecimal total = jpaRepository.sumCurrentQuantityByProductId(productId);
        log.debug("Total stock for productId={} is {}", productId, total);
        return total;
    }

    @Override
    public Stock save(Stock stock) {
        log.info("Saving stock batch for productId={} quantity={}", stock.getProductId(), stock.getQuantity());
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
