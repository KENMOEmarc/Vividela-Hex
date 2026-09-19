package ken.vivid.domain.entities;

import ken.vivid.support.StockTestBuilder;
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
    @DisplayName("Creates a valid batch")
    void createStockShouldBuildAValidLot() {
        Instant expiration = entry.plus(30, ChronoUnit.DAYS);

        Stock batch = StockTestBuilder.aBatch()
                .withId(1L).withProductId(42L)
                .withQuantity(new BigDecimal("15.5"))
                .withUnitPrice(new BigDecimal("500"))
                .withEntryDate(entry).
                withExpirationDate(expiration).build();

        assertThat(batch.getId()).isEqualTo(1L);
        assertThat(batch.getProductId()).isEqualTo(42L);
        assertThat(batch.getQuantity()).isEqualByComparingTo("15.5");
        assertThat(batch.getUnitPrice()).isEqualByComparingTo("500");
        assertThat(batch.getEntryDate()).isEqualTo(entry);
        assertThat(batch.getExpirationDate()).isEqualTo(expiration);
    }

    @Test
    @DisplayName("Accepts a zero quantity (depleted batch)")
    void createStockShouldAcceptZeroQuantity() {
        Stock batch = aBatch().withQuantity(BigDecimal.ZERO).build();

        assertThat(batch.getQuantity()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Rejects a negative quantity")
    void createStockShouldRejectNegativeQuantity() {
        assertThatThrownBy(() -> StockTestBuilder.aBatch().withQuantity(new BigDecimal("-1")).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity");
    }

    @Test
    @DisplayName("Rejects a negative unit price")
    void createStockShouldRejectNegativeUnitPrice() {
        assertThatThrownBy(() -> StockTestBuilder.aBatch().withUnitPrice(new BigDecimal("-0.01")).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unit price");
    }

    @Test
    @DisplayName("Rejects an expiration date earlier than the entry date")
    void createStockShouldRejectExpirationBeforeEntryDate() {
        assertThatThrownBy(() -> StockTestBuilder.aBatch().withExpirationDate(entry.minus(1, ChronoUnit.DAYS)).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expiration date");
    }

    @Test
    @DisplayName("Rejects an update date earlier than the entry date")
    void createStockShouldRejectUpdatedAtBeforeEntryDate() {
        assertThatThrownBy(() -> StockTestBuilder.aBatch().withUpdatedAt(entry.minusSeconds(1)).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Updated at");
    }

    @Test
    @DisplayName("Rejects a null identifier")
    void createStockShouldRejectNullId() {
        assertThatThrownBy(() -> StockTestBuilder.aBatch().withId(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");
    }

    @Test
    @DisplayName("Rejects a null product identifier")
    void createStockShouldRejectNullProductId() {
        assertThatThrownBy(() -> StockTestBuilder.aBatch().withProductId(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product ID");
    }

    @Test
    @DisplayName("Rejects a null expiration date")
    void createStockShouldRejectNullExpirationDate() {
        assertThatThrownBy(() -> StockTestBuilder.aBatch().withExpirationDate(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expiration date cannot be null");
    }

    @Test
    @DisplayName("Quantity and update date remain mutable after creation")
    void settersShouldUpdateQuantityAndUpdatedAt() {
        Stock batch = aBatch().withQuantity("10").build();
        Instant now = Instant.now();

        batch.setQuantity(new BigDecimal("4"));
        batch.setUpdatedAt(now);

        assertThat(batch.getQuantity()).isEqualByComparingTo("4");
        assertThat(batch.getUpdatedAt()).isEqualTo(now);
    }
}