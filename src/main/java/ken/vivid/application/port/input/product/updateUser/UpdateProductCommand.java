package ken.vivid.application.port.input.product.updateUser;

import ken.vivid.domain.dto.MeasurementUnit;

import java.math.BigDecimal;

public record UpdateProductCommand(
        Long productId,
        Long actingUserId,
        String name,
        BigDecimal thresholdValue,
        MeasurementUnit measurementUnit
) {
}
