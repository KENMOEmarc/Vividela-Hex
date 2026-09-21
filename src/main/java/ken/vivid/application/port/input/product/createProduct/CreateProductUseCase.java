package ken.vivid.application.port.input.product.createProduct;

import ken.vivid.domain.entities.product.Product;

public interface CreateProductUseCase {
    Product create(CreateProductCommand command);
}
