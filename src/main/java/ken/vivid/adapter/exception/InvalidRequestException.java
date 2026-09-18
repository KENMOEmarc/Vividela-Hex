package ken.vivid.adapter.exception;

import ken.vivid.domain.exception.DomainException;

public class InvalidRequestException extends DomainException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
