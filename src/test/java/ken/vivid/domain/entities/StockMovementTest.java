package ken.vivid.domain.entities;

import ken.vivid.domain.dto.MovementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit tests for the domain entity {@link StockMovement}.
 * <p>
 * The stock movement is the store's audit trail: each invariant
 * rejected here corresponds to a traceability line that would be unusable.
 */
@DisplayName("StockMovement (domain)")
class StockMovementTest {

    private static final Instant NOW = Instant.now().minusSeconds(5);

    @Test
    @DisplayName("Creates a valid movement")
    void createStockMovementShouldBuildAValidMovement() {
        StockMovement movement = StockMovement.createStockMovement(1L, 10L, 99L,
                new BigDecimal("3.5"), MovementType.CONSUMPTION, "Workshop consumption", NOW);

        assertThat(movement.getId()).isEqualTo(1L);
        assertThat(movement.getStockId()).isEqualTo(10L);
        assertThat(movement.getUserId()).isEqualTo(99L);
        assertThat(movement.getQuantity()).isEqualByComparingTo("3.5");
        assertThat(movement.getMovementType()).isEqualTo(MovementType.CONSUMPTION);
        assertThat(movement.getNotes()).isEqualTo("Workshop consumption");
        assertThat(movement.getMovementDate()).isEqualTo(NOW);
    }

    @Test
    @DisplayName("Rejects a null identifier")
    void createStockMovementShouldRejectNullId() {
        assertThatThrownBy(() -> StockMovement.createStockMovement(null, 10L, 99L,
                BigDecimal.ONE, MovementType.RESTOCK, "note", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");
    }

    @Test
    @DisplayName("Rejects a null batch identifier")
    void createStockMovementShouldRejectNullStockId() {
        assertThatThrownBy(() -> StockMovement.createStockMovement(1L, null, 99L,
                BigDecimal.ONE, MovementType.RESTOCK, "note", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock ID");
    }

    @Test
    @DisplayName("Rejects a null user identifier (no anonymous movement)")
    void createStockMovementShouldRejectNullUserId() {
        assertThatThrownBy(() -> StockMovement.createStockMovement(1L, 10L, null,
                BigDecimal.ONE, MovementType.RESTOCK, "note", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID");
    }

    @Test
    @DisplayName("Rejects a negative quantity")
    void createStockMovementShouldRejectNegativeQuantity() {
        assertThatThrownBy(() -> StockMovement.createStockMovement(1L, 10L, 99L,
                new BigDecimal("-1"), MovementType.RESTOCK, "note", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity");
    }

    @Test
    @DisplayName("Rejects a null movement type")
    void createStockMovementShouldRejectNullMovementType() {
        assertThatThrownBy(() -> StockMovement.createStockMovement(1L, 10L, 99L,
                BigDecimal.ONE, null, "note", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Movement type");
    }

    @ParameterizedTest(name = "invalid note: \"{0}\"")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("Rejects a null or blank note")
    void createStockMovementShouldRejectBlankNotes(String invalidNote) {
        assertThatThrownBy(() -> StockMovement.createStockMovement(1L, 10L, 99L,
                BigDecimal.ONE, MovementType.RESTOCK, invalidNote, NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Notes");
    }

    @Test
    @DisplayName("Rejects a movement date in the future")
    void createStockMovementShouldRejectFutureMovementDate() {
        assertThatThrownBy(() -> StockMovement.createStockMovement(1L, 10L, 99L,
                BigDecimal.ONE, MovementType.ADJUSTMENT, "note", Instant.now().plusSeconds(3600)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Movement date");
    }

    @Test
    @DisplayName("Rejects a null movement date")
    void createStockMovementShouldRejectNullMovementDate() {
        assertThatThrownBy(() -> StockMovement.createStockMovement(1L, 10L, 99L,
                BigDecimal.ONE, MovementType.ADJUSTMENT, "note", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Movement date");
    }
}