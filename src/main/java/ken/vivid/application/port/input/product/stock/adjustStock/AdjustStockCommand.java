package ken.vivid.application.port.input.product.stock.adjustStock;

import java.math.BigDecimal;

public record AdjustStockCommand(
        Long stockId, Long actingUserId,
        BigDecimal newStockLevel, String notes) {
}
