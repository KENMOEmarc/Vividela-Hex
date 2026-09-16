package ken.vivid.domain.port.output;

import ken.vivid.domain.entities.ProductRegistration;

public interface SaveProductRegistration {
    ProductRegistration save(ProductRegistration registration);
}
