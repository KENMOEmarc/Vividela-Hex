package ken.vivid.adapter.exception;

import ken.vivid.domain.exception.DomainException;

public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
