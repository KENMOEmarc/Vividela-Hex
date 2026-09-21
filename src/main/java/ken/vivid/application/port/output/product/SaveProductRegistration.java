package ken.vivid.application.port.output.product;

import ken.vivid.domain.entities.product.ProductRegistration;

public interface SaveProductRegistration {
    ProductRegistration save(ProductRegistration registration);
}
