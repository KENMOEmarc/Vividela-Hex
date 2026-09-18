package ken.vivid.application.port.input.product.createProduct;

import ken.vivid.domain.entities.Product;

public interface CreateProductUseCase {
    Product create(CreateProductCommand command);
}
