package ken.vivid.application.port.input.product.updateUser;

import ken.vivid.domain.entities.Product;

public interface UpdateProductUseCase {
    Product update(UpdateProductCommand command);
}
