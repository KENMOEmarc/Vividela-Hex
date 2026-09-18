package ken.vivid.application.port.output.product;

import ken.vivid.domain.entities.Product;

import java.util.List;
import java.util.Optional;

public interface LoadProduct {
    Optional<Product> loadById(Long id);

    Optional<Product> loadByName(String name);

    List<Product> loadAll();

    boolean existsByName(String name);
}
