package ken.vivid.domain.port.output;

import ken.vivid.domain.entities.Stock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LoadStock {
    Optional<Stock> loadById(Long id);

    List<Stock> loadAvailableByProductOrderedByExpiration(Long productId);

    BigDecimal totalQuantityByProduct(Long productId);

}
