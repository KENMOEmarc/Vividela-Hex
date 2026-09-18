package ken.vivid.adapter.exception;

import ken.vivid.domain.exception.DomainException;

public class DuplicateResourceException extends DomainException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
