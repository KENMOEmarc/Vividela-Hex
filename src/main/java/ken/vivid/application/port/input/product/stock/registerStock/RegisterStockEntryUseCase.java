package ken.vivid.application.port.input.product.stock.registerStock;

import ken.vivid.domain.entities.product.Stock;

public interface RegisterStockEntryUseCase {
    Stock register(RegisterStockCommand command);
}
