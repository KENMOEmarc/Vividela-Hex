package ken.vivid.domain.port.input;

import java.math.BigDecimal;

public record ConsumeStockCommand(
        Long productId,
        Long actingUserId,
        BigDecimal quantity,
        String notes
) {
}
