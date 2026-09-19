package ken.vivid.support;

import ken.vivid.domain.entities.Stock;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Test builder for {@link Stock} (stock batch).
 * <p>
 * Warning: {@code Stock.createStock} throws a {@code NullPointerException} if
 * {@code expirationDate} is null (call to {@code expirationDate.isBefore(...)}
 * without null checking). The builder therefore always provides a valid
 * expiration date by default, and {@link #withoutExpirationDate()} forces the
 * field to null via reflection: this is the only way to build a batch
 * "without expiration" and to cover the {@code nullsLast} branch of
 * {@code StockAllocationPolicy}.
 */
public final class StockTestBuilder {

    private Long id = 1L;
    private Long productId = 1L;
    private BigDecimal quantity = BigDecimal.TEN;
    private BigDecimal unitPrice = new BigDecimal("100");
    private Instant entryDate = Instant.now().minus(1, ChronoUnit.DAYS);
    private Instant updatedAt = Instant.now();
    private Instant expirationDate = Instant.now().plus(30, ChronoUnit.DAYS);
    private boolean withoutExpirationDate = false;

    private StockTestBuilder() {
    }

    public static StockTestBuilder aBatch() {
        return new StockTestBuilder();
    }

    public StockTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public StockTestBuilder withProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public StockTestBuilder withQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        return this;
    }

    public StockTestBuilder withQuantity(String quantity) {
        return withQuantity(new BigDecimal(quantity));
    }

    public StockTestBuilder withUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public StockTestBuilder withEntryDate(Instant entryDate) {
        this.entryDate = entryDate;
        return this;
    }

    public StockTestBuilder withUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public StockTestBuilder withExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
        this.withoutExpirationDate = false;
        return this;
    }

    /** Expiration date expressed as a number of days from now. */
    public StockTestBuilder expiringInNDays(long days) {
        return withExpirationDate(Instant.now().plus(days, ChronoUnit.DAYS));
    }

    /** Batch without expiration date (field forced to null, see class javadoc). */
    public StockTestBuilder withoutExpirationDate() {
        this.withoutExpirationDate = true;
        return this;
    }
    public StockTestBuilder expiringInDays(int i) {
        expirationDate.plus(i, ChronoUnit.DAYS);
        return this;
    }

    public Stock build() {
        Stock stock = Stock.createStock(id, productId, quantity, updatedAt, entryDate, unitPrice, expirationDate);
        if (withoutExpirationDate) {
            forceFieldToNull(stock, "expirationDate");
        }
        return stock;
    }

    private static void forceFieldToNull(Stock stock, String fieldName) {
        try {
            Field field = Stock.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(stock, null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to force field " + fieldName + " to null", e);
        }
    }

}