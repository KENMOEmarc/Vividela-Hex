package ken.vivid.application.port.output.product.stock;

import ken.vivid.domain.entities.Stock;

public interface SaveStock {
    Stock save(Stock stock);
}
