package ken.vivid.application.port.output.serviceprice;

import ken.vivid.domain.entities.service.ServicePrice;

public interface SaveServicePrice {
    ServicePrice save(ServicePrice servicePrice);
}
