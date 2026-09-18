package ken.vivid.adapter.exception.auth;

import ken.vivid.adapter.exception.DuplicateResourceException;

public class UserAlreadyExistsException extends DuplicateResourceException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
