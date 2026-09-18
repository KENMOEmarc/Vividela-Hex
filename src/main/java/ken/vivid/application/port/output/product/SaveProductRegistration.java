package ken.vivid.application.port.output.product;

import ken.vivid.domain.entities.ProductRegistration;

public interface SaveProductRegistration {
    ProductRegistration save(ProductRegistration registration);
}
