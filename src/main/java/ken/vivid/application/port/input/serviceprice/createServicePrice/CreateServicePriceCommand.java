package ken.vivid.application.port.input.serviceprice.createServicePrice;

import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;

import java.math.BigDecimal;

public record CreateServicePriceCommand(ClothingType clothingType, ServiceType service, BigDecimal price) {}
