package ken.vivid.application.port.input.serviceprice;

import ken.vivid.domain.entities.service.ServicePrice;

public interface ManageServicePriceStatusUseCase {
    ServicePrice activate(Long id);
    ServicePrice deactivate(Long id);
}
