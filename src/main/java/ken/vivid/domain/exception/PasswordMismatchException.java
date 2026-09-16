package ken.vivid.domain.exception;

public class PasswordMismatchException extends InvalidRequestException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
