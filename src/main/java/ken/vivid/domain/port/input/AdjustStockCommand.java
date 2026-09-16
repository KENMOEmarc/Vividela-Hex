package ken.vivid.domain.port.input;

import java.math.BigDecimal;

public record AdjustStockCommand(
        Long stockId, Long actingUserId,
        BigDecimal newStockLevel, String notes) {
}
