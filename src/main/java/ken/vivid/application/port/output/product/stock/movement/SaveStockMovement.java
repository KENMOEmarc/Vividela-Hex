package ken.vivid.application.port.output.product.stock.movement;

import ken.vivid.domain.entities.StockMovement;

public interface SaveStockMovement {
    StockMovement save(StockMovement movement);
}
