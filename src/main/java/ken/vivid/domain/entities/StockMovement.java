package ken.vivid.domain.entities;

import ken.vivid.domain.dto.MovementType;

import java.math.BigDecimal;
import java.time.Instant;

public class StockMovement {
    private final Long id;
    private final Long stockId;
    private final Long userId;
    private BigDecimal quantity;
    private MovementType movementType;
    private String notes;
    private Instant movementDate;

    private StockMovement(Long id, Long stockId, Long userId, BigDecimal quantity,
                          MovementType movementType, String notes, Instant movementDate) {
        this.id = id;
        this.stockId = stockId;
        this.userId = userId;
        this.quantity = quantity;
        this.movementType = movementType;
        this.notes = notes;
        this.movementDate = movementDate;
    }

    public static StockMovement createStockMovement(Long id, Long stockId, Long userId, BigDecimal quantity,
                                                    MovementType movementType, String notes, Instant movementDate) {

        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (stockId == null) {
            throw new IllegalArgumentException("Stock ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity cannot be null or negative");
        }
        if (movementType == null) {
            throw new IllegalArgumentException("Movement type cannot be null");
        }
        if (notes == null || notes.isBlank()) {
            throw new IllegalArgumentException("Notes cannot be null or blank");
        }
        if (movementDate == null || movementDate.isAfter(Instant.now())) {
            throw new IllegalArgumentException("Movement date cannot be null or in the future");
        }

        return new StockMovement(id, stockId, userId, quantity, movementType, notes, movementDate);
    }

    public Long getId() {
        return id;
    }

    public Long getStockId() {
        return stockId;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getMovementDate() {
        return movementDate;
    }
}
