package ken.vivid.adapter.exception.product;

import ken.vivid.adapter.exception.InvalidRequestException;

public class InsufficientStockException extends InvalidRequestException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
