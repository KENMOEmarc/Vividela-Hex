package ken.vivid.adapter.exception;

import ken.vivid.domain.exception.DomainException;

public class InvalidStateTransitionException extends DomainException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
