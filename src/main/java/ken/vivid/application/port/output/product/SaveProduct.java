package ken.vivid.application.port.output.product;

import ken.vivid.domain.entities.product.Product;

public interface SaveProduct {
    Product save(Product product);
}
