package ken.vivid.application.port.input.product;

import ken.vivid.domain.entities.Product;

import java.util.List;

public interface GetProductUseCase {
    Product getById(Long productId);
    List<Product> getAllProducts();
}
