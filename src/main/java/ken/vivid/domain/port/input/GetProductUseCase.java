package ken.vivid.domain.port.input;

import ken.vivid.domain.entities.Product;

import java.util.List;

public interface GetProductUseCase {
    Product getById(Long productId);
    List<Product> getAllProducts();
}
