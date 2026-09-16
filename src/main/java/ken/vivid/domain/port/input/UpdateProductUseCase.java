package ken.vivid.domain.port.input;

import ken.vivid.domain.entities.Product;

public interface UpdateProductUseCase {
    Product update(UpdateProductCommand command);
}
