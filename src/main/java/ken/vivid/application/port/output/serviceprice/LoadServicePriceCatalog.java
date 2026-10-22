package ken.vivid.application.port.output.serviceprice;

import ken.vivid.domain.dto.ClothingType;
import ken.vivid.domain.dto.ServiceType;
import ken.vivid.domain.entities.service.ServicePrice;

import java.util.List;
import java.util.Optional;

/** Read access to the full price list (entries, active or not). */
public interface LoadServicePriceCatalog {
    Optional<ServicePrice> loadById(Long id);
    List<ServicePrice> loadAll();
    List<ServicePrice> loadAllActive();
    List<ServicePrice> loadByClothingType(ClothingType clothingType);
    boolean existsByClothingTypeAndService(ClothingType clothingType, ServiceType service);
}
