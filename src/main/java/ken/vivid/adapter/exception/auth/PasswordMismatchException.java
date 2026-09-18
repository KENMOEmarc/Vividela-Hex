package ken.vivid.adapter.exception.auth;

import ken.vivid.adapter.exception.InvalidRequestException;

public class PasswordMismatchException extends InvalidRequestException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
