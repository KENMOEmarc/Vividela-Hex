package ken.vivid.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static ken.vivid.support.StockTestBuilder.aBatch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit tests for the domain entity {@link Stock} (stock batch).
 */
@DisplayName("Stock (domain)")
class StockTest {

    private final Instant entry = Instant.now().minus(2, ChronoUnit.DAYS);

    @Test
    @DisplayName("creates a valid batch")
    void createStockShouldBuildAValidLot() {
        Instant expiration = entry.plus(30, ChronoUnit.DAYS);

        Stock batch = Stock.createStock(1L, 42L, new BigDecimal("15.5"), entry.plusSeconds(60),
                entry, new BigDecimal("500"), expiration);

        assertThat(batch.getId()).isEqualTo(1L);
        assertThat(batch.getProductId()).isEqualTo(42L);
        assertThat(batch.getQuantity()).isEqualByComparingTo("15.5");
        assertThat(batch.getUnitPrice()).isEqualByComparingTo("500");
        assertThat(batch.getEntryDate()).isEqualTo(entry);
        assertThat(batch.getExpirationDate()).isEqualTo(expiration);
    }

    @Test
    @DisplayName("accepts a zero quantity (depleted batch)")
    void createStockShouldAcceptZeroQuantity() {
        Stock batch = aBatch().withQuantity(BigDecimal.ZERO).build();

        assertThat(batch.getQuantity()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("rejects a negative quantity")
    void createStockShouldRejectNegativeQuantity() {
        assertThatThrownBy(() -> Stock.createStock(1L, 42L, new BigDecimal("-1"), entry.plusSeconds(60),
                entry, BigDecimal.TEN, entry.plus(1, ChronoUnit.DAYS)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity");
    }

    @Test
    @DisplayName("rejects a negative unit price")
    void createStockShouldRejectNegativeUnitPrice() {
        assertThatThrownBy(() -> Stock.createStock(1L, 42L, BigDecimal.TEN, entry.plusSeconds(60),
                entry, new BigDecimal("-0.01"), entry.plus(1, ChronoUnit.DAYS)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unit price");
    }

    @Test
    @DisplayName("rejects an expiration date earlier than the entry date")
    void createStockShouldRejectExpirationBeforeEntryDate() {
        assertThatThrownBy(() -> Stock.createStock(1L, 42L, BigDecimal.TEN, entry.plusSeconds(60),
                entry, BigDecimal.TEN, entry.minus(1, ChronoUnit.DAYS)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expiration date");
    }

    @Test
    @DisplayName("rejects an update date earlier than the entry date")
    void createStockShouldRejectUpdatedAtBeforeEntryDate() {
        assertThatThrownBy(() -> Stock.createStock(1L, 42L, BigDecimal.TEN, entry.minusSeconds(1),
                entry, BigDecimal.TEN, entry.plus(1, ChronoUnit.DAYS)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Updated at");
    }

    @Test
    @DisplayName("rejects a null identifier")
    void createStockShouldRejectNullId() {
        assertThatThrownBy(() -> Stock.createStock(null, 42L, BigDecimal.TEN, entry.plusSeconds(60),
                entry, BigDecimal.TEN, entry.plus(1, ChronoUnit.DAYS)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");
    }

    @Test
    @DisplayName("rejects a null product identifier")
    void createStockShouldRejectNullProductId() {
        assertThatThrownBy(() -> Stock.createStock(1L, null, BigDecimal.TEN, entry.plusSeconds(60),
                entry, BigDecimal.TEN, entry.plus(1, ChronoUnit.DAYS)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product ID");
    }

    /*
     * Characterization test: it documents the CURRENT behavior, not the
     * desired behavior. createStock calls expirationDate.isBefore(...)
     * without null checking: a batch without expiration therefore throws an NPE,
     * whereas StockAllocationPolicy explicitly handles this case
     * (Comparator.nullsLast). To be fixed on the domain side; this test should then
     * be replaced by a successful creation assertion.
     */
    @Test
    @DisplayName("[known anomaly] a null expiration date throws a NullPointerException")
    void createStockShouldCurrentlyThrowNpeWhenExpirationDateIsNull() {
        assertThatThrownBy(() -> Stock.createStock(1L, 42L, BigDecimal.TEN, entry.plusSeconds(60),
                entry, BigDecimal.TEN, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("quantity and update date remain mutable after creation")
    void settersShouldUpdateQuantityAndUpdatedAt() {
        Stock batch = aBatch().withQuantity("10").build();
        Instant now = Instant.now();

        batch.setQuantity(new BigDecimal("4"));
        batch.setUpdatedAt(now);

        assertThat(batch.getQuantity()).isEqualByComparingTo("4");
        assertThat(batch.getUpdatedAt()).isEqualTo(now);
    }
}