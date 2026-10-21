package ken.vivid.application.port.output.article;

import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;

import java.math.BigDecimal;
import java.util.Optional;

public interface LoadServicePrice {
    Optional<BigDecimal> loadActivePrice(ClothingType clothingType, ServiceType service);
}
