package ken.vivid.domain.port.input;

import ken.vivid.domain.entities.Product;

public interface CreateProductUseCase {
    Product create(CreateProductCommand command);
}
