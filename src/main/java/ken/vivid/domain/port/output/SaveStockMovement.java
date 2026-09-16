package ken.vivid.domain.port.output;

import ken.vivid.domain.entities.StockMovement;

public interface SaveStockMovement {
    StockMovement save(StockMovement movement);
}
