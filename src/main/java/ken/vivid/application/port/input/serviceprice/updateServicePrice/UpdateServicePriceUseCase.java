package ken.vivid.application.port.input.serviceprice.updateServicePrice;

import ken.vivid.domain.entities.service.ServicePrice;

public interface UpdateServicePriceUseCase {
    ServicePrice update(UpdateServicePriceCommand command);
}
