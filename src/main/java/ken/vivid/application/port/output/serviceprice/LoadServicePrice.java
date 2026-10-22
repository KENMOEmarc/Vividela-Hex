package ken.vivid.application.port.output.serviceprice;

import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Read access to the price list, as needed by the Article module.
 * Returns the price of an ACTIVE entry only; empty when the combination
 * is unknown or deactivated. Implemented by the ServicePrice module's
 * persistence adapter.
 */
public interface LoadServicePrice {
    Optional<BigDecimal> loadActivePrice(ClothingType clothingType, ServiceType service);
}
