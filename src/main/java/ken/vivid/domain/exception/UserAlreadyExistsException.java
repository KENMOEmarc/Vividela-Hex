package ken.vivid.domain.exception;

public class UserAlreadyExistsException extends DuplicateResourceException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
