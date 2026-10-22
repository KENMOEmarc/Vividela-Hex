package ken.vivid.application.port.input.serviceprice.createServicePrice;

import ken.vivid.domain.entities.service.ServicePrice;

public interface CreateServicePriceUseCase {
    ServicePrice create(CreateServicePriceCommand command);
}
