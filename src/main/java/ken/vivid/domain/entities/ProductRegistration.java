package ken.vivid.domain.entities;

import ken.vivid.domain.dto.RegistrationType;

import java.math.BigDecimal;
import java.time.Instant;

public class ProductRegistration {
    private final Long id;
    private final Long productId;
    private BigDecimal quantity;
    private RegistrationType registrationType;
    private String notes;
    private Instant registeredAt;

    private ProductRegistration(Long id, Long productId, BigDecimal quantity,
                                RegistrationType registrationType, String notes, Instant registeredAt) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.registrationType = registrationType;
        this.notes = notes;
        this.registeredAt = registeredAt;
    }

    public static ProductRegistration createProductRegistration(Long id, Long productId, BigDecimal quantity,
                                                                RegistrationType registrationType, String notes, Instant registeredAt) {

        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity cannot be null or negative");
        }
        if (registrationType == null) {
            throw new IllegalArgumentException("Registration type cannot be null");
        }
        if (notes == null || notes.trim().isEmpty()) {
            throw new IllegalArgumentException("Notes cannot be null or empty");
        }
        if (registeredAt == null || registeredAt.isAfter(Instant.now())) {
            throw new IllegalArgumentException("Registered at date cannot be null or in the future");
        }

        return new ProductRegistration(id, productId, quantity, registrationType, notes, registeredAt);
    }

    public Long getProductId() {
        return productId;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public RegistrationType getRegistrationType() {
        return registrationType;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }
}
