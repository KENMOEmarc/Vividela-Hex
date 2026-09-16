package ken.vivid.domain.port.input;

import ken.vivid.domain.dto.MeasurementUnit;

import java.math.BigDecimal;

public record CreateProductCommand(
        String name,
        BigDecimal thresholdValue,
        MeasurementUnit measurementUnit
) {
}
