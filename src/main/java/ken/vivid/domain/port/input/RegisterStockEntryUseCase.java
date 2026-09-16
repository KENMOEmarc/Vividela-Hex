package ken.vivid.domain.port.input;

import ken.vivid.domain.entities.Stock;

public interface RegisterStockEntryUseCase {
    Stock register(RegisterStockCommand command);
}
