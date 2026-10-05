package ken.vivid.domain.entities.product;

import java.math.BigDecimal;
import java.time.Instant;

public class Stock {
    private final Long id;
    private final Long productId;
    private BigDecimal quantity;
    private final BigDecimal unitPrice;
    private final Instant entryDate;
    private final Instant expirationDate;
    private final Instant createdAt;
    private Instant updatedAt;

    private Stock(Long id, Long productId, BigDecimal quantity, Instant updatedAt,
                  Instant entryDate, BigDecimal unitPrice, Instant expirationDate, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.updatedAt = updatedAt;
        this.entryDate = entryDate;
        this.unitPrice = unitPrice;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
    }

    public static Stock createStock(Long id, Long productId, BigDecimal quantity, Instant updatedAt,
                                    Instant entryDate, BigDecimal unitPrice, Instant expirationDate) {
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }

        if (expirationDate == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }

        if (expirationDate.isBefore(entryDate)) {
            throw new IllegalArgumentException("Expiration date cannot be before entry date");
        }

        if(updatedAt == null) {
            throw new IllegalArgumentException("Updated at date cannot be null");
        }

        if (updatedAt.isBefore(entryDate)) {
            throw new IllegalArgumentException("Updated at date cannot be before entry date");
        }

        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        return new Stock(id, productId, quantity, updatedAt, entryDate, unitPrice, expirationDate, Instant.now());
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Instant getEntryDate() {
        return entryDate;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
