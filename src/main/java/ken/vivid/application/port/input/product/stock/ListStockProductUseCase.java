package ken.vivid.application.port.input.product.stock;

import ken.vivid.domain.entities.Product;

import java.util.List;

public interface ListStockProductUseCase {
    List<Product> listStockProducts();
}
