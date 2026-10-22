package ken.vivid.application.port.input.serviceprice;

import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.entities.service.ServicePrice;

import java.util.List;

public interface GetServicePriceUseCase {
    ServicePrice getById(Long id);

    /**
     * @param clothingType optional filter; {@code null} means all clothing types
     * @param activeOnly   when true, inactive entries are left out
     */
    List<ServicePrice> getAll(ClothingType clothingType, boolean activeOnly);
}
