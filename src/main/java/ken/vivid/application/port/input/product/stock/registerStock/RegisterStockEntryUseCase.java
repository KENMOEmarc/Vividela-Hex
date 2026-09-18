package ken.vivid.application.port.input.product.stock.registerStock;

import ken.vivid.domain.entities.Stock;

public interface RegisterStockEntryUseCase {
    Stock register(RegisterStockCommand command);
}
