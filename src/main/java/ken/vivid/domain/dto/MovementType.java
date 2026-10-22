package ken.vivid.domain.dto;

public enum MovementType {
    CONSUMPTION,
    RESTOCK,
    ADJUSTMENT;

    public enum OrderStatus {
        RECEIVED,
        PENDING,
        IN_PROGRESS,
        READY,
        DELIVERED,
        CANCELLED
    }
}
