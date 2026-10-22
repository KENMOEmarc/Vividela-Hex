package ken.vivid.application.port.input.serviceprice.updateServicePrice;

import java.math.BigDecimal;

public record UpdateServicePriceCommand(Long servicePriceId, BigDecimal price) {}
