package ken.vivid.domain.port.output;

import ken.vivid.domain.entities.Product;

public interface SaveProduct {
    Product save(Product product);
}
