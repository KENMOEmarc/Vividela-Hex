package ken.vivid.application.port.input.product.stock.consumeStock;

import java.math.BigDecimal;

public record ConsumeStockCommand(
        Long productId,
        Long actingUserId,
        BigDecimal quantity,
        String notes
) {
}
