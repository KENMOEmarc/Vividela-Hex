package ken.vivid.application.port.input.product.updateUser;

import ken.vivid.domain.entities.product.Product;

public interface UpdateProductUseCase {
    Product update(UpdateProductCommand command);
}
