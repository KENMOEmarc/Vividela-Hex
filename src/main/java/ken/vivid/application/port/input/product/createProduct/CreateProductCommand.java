package ken.vivid.application.port.input.product.createProduct;

import ken.vivid.domain.dto.MeasurementUnit;

import java.math.BigDecimal;

public record CreateProductCommand(
        String name,
        BigDecimal thresholdValue,
        MeasurementUnit measurementUnit
) {
}
