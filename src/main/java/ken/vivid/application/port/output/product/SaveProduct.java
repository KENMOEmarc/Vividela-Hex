package ken.vivid.application.port.output.product;

import ken.vivid.domain.entities.Product;

public interface SaveProduct {
    Product save(Product product);
}
