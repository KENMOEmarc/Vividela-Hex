package ken.vivid.application.service.product;

import ken.vivid.adapter.exception.InvalidRequestException;
import ken.vivid.adapter.exception.product.InsufficientStockException;
import ken.vivid.domain.entities.product.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static ken.vivid.support.StockTestBuilder.aBatch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the FEFO allocation policy ({@link StockAllocationPolicy}).
 * <p>
 * This is the richest business rule in the project and it has no external
 * dependency: it can be tested with a simple {@code new}, without Spring or mocks.
 */
@DisplayName("StockAllocationPolicy (FEFO)")
class StockAllocationPolicyTest {

    private final StockAllocationPolicy policy = new StockAllocationPolicy();

    @Test
    @DisplayName("rejects a null quantity")
    void allocateShouldRejectNullQuantity() {
        List<Stock> batches = List.of(aBatch().build());

        assertThatThrownBy(() -> policy.allocate(batches, null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("strictly positive");
    }

    @Test
    @DisplayName("rejects a zero or negative quantity")
    void allocateShouldRejectNonPositiveQuantity() {
        List<Stock> batches = List.of(aBatch().build());

        assertThatThrownBy(() -> policy.allocate(batches, BigDecimal.ZERO))
                .isInstanceOf(InvalidRequestException.class);
        assertThatThrownBy(() -> policy.allocate(batches, new BigDecimal("-3")))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    @DisplayName("consumes the batch with the earliest expiration date first")
    void allocateShouldConsumeTheSoonestExpiringLotFirst() {
        Stock expiringSoon = aBatch().withId(1L).withQuantity("5").expiringInDays(1).build();
        Stock expiringLater = aBatch().withId(2L).withQuantity("5").expiringInDays(30).build();

        // deliberately unsorted list: the policy must sort it itself
        List<StockAllocationPolicy.Allocation> allocations =
                policy.allocate(List.of(expiringLater, expiringSoon), new BigDecimal("3"));

        assertThat(allocations).hasSize(1);
        assertThat(allocations.get(0).stock().getId()).isEqualTo(1L);
        assertThat(allocations.get(0).quantityToConsume()).isEqualByComparingTo("3");
    }

    @Test
    @DisplayName("splits the consumption across several batches when the first one is not enough")
    void allocateShouldSplitAcrossLotsWhenOneIsNotEnough() {
        Stock batch1 = aBatch().withId(1L).withQuantity("3").expiringInDays(1).build();
        Stock batch2 = aBatch().withId(2L).withQuantity("10").expiringInDays(10).build();

        List<StockAllocationPolicy.Allocation> allocations =
                policy.allocate(List.of(batch1, batch2), new BigDecimal("5"));

        assertThat(allocations).hasSize(2);
        assertThat(allocations.get(0).stock().getId()).isEqualTo(1L);
        assertThat(allocations.get(0).quantityToConsume()).isEqualByComparingTo("3");
        assertThat(allocations.get(1).stock().getId()).isEqualTo(2L);
        assertThat(allocations.get(1).quantityToConsume()).isEqualByComparingTo("2");
    }

    @Test
    @DisplayName("consumes batches without an expiration date last")
    void allocateShouldConsumeLotsWithoutExpirationDateLast() {
        Stock noExpiration = aBatch().withId(1L).withQuantity("10").withoutExpirationDate().build();
        Stock withExpiration = aBatch().withId(2L).withQuantity("10").expiringInDays(5).build();

        List<StockAllocationPolicy.Allocation> allocations =
                policy.allocate(List.of(noExpiration, withExpiration), new BigDecimal("15"));

        assertThat(allocations).extracting(a -> a.stock().getId()).containsExactly(2L, 1L);
        assertThat(allocations.get(0).quantityToConsume()).isEqualByComparingTo("10");
        assertThat(allocations.get(1).quantityToConsume()).isEqualByComparingTo("5");
    }

    @Test
    @DisplayName("ignores empty or depleted batches")
    void allocateShouldIgnoreLotsWithoutAvailableQuantity() {
        Stock depleted = aBatch().withId(1L).withQuantity("0").expiringInDays(1).build();
        Stock usable = aBatch().withId(2L).withQuantity("5").expiringInDays(2).build();

        List<StockAllocationPolicy.Allocation> allocations =
                policy.allocate(List.of(depleted, usable), new BigDecimal("3"));

        assertThat(allocations).extracting(a -> a.stock().getId()).containsExactly(2L);
    }

    @Test
    @DisplayName("stops as soon as the requested quantity is covered")
    void allocateShouldStopAsSoonAsTheRequestedQuantityIsCovered() {
        Stock batch1 = aBatch().withId(1L).withQuantity("10").expiringInDays(1).build();
        Stock batch2 = aBatch().withId(2L).withQuantity("10").expiringInDays(2).build();
        Stock batch3 = aBatch().withId(3L).withQuantity("10").expiringInDays(3).build();

        List<StockAllocationPolicy.Allocation> allocations =
                policy.allocate(List.of(batch1, batch2, batch3), new BigDecimal("12"));

        // only the minimum number of batches is touched: 2 stock movements instead of 3
        assertThat(allocations).hasSize(2);
    }

    @Test
    @DisplayName("consumes exactly one whole batch without starting a second one")
    void allocateShouldConsumeAWholeLotExactly() {
        Stock batch1 = aBatch().withId(1L).withQuantity("5").expiringInDays(1).build();
        Stock batch2 = aBatch().withId(2L).withQuantity("5").expiringInDays(2).build();

        List<StockAllocationPolicy.Allocation> allocations =
                policy.allocate(List.of(batch1, batch2), new BigDecimal("5"));

        assertThat(allocations).hasSize(1);
        assertThat(allocations.get(0).stock().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("fails when the available stock does not cover the request")
    void allocateShouldThrowInsufficientStockWhenTotalIsBelowRequest() {
        Stock batch = aBatch().withId(1L).withQuantity("2").expiringInDays(1).build();

        assertThatThrownBy(() -> policy.allocate(List.of(batch), BigDecimal.TEN))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("missing");
    }

    @Test
    @DisplayName("fails on an empty or null batch list")
    void allocateShouldThrowInsufficientStockWhenNoLotIsAvailable() {
        assertThatThrownBy(() -> policy.allocate(List.of(), BigDecimal.ONE))
                .isInstanceOf(InsufficientStockException.class);

        assertThatThrownBy(() -> policy.allocate(null, BigDecimal.ONE))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    @DisplayName("does not modify the batches: allocation is a pure computation")
    void allocateShouldNotMutateTheGivenLots() {
        Stock batch = aBatch().withId(1L).withQuantity("10").expiringInDays(1).build();

        policy.allocate(List.of(batch), new BigDecimal("4"));

        // the actual debit is the responsibility of StockService, not of the policy
        assertThat(batch.getQuantity()).isEqualByComparingTo("10");
    }
}