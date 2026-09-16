package ken.vivid.domain.port.output;

import ken.vivid.domain.entities.Stock;

public interface SaveStock {
    Stock save(Stock stock);
}
