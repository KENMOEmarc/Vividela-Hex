package ken.vivid.adapter.input.web;

import ken.vivid.domain.exception.InvalidCredentialsException;
import ken.vivid.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<?extends DomainException>, HttpStatus> STATUS_BY_EXCEPTION = Map.of(
      ResourceNotFoundException.class,  HttpStatus.NOT_FOUND,
            DuplicateResourceException.class, HttpStatus.CONFLICT,
            InvalidRequestException.class, HttpStatus.BAD_REQUEST,
            InvalidCredentialsException.class, HttpStatus.FORBIDDEN,
            InvalidStateTransitionException.class, HttpStatus.CONFLICT
    );

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest()
                .body(ApiResponse.<Void>builder()
                        .errors(errors).build()
                );
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException ex) {
        HttpStatus status = resolveStatus(ex.getClass());
        log.info("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity.status(status).body(ApiResponse.error(ex.getMessage()));
    }

    private HttpStatus resolveStatus(Class<?> exceptionClass) {
        Class<?> current = exceptionClass;
        while (current != null && DomainException.class.isAssignableFrom(current)) {
            HttpStatus status = STATUS_BY_EXCEPTION.get(current);
            if (status != null) return status;
            current = current.getSuperclass();
        }
        return HttpStatus.CONFLICT; //Response to uncatalogued Exceptions
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access denied : insufficient permissions"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("This operation is in conflict with an existing data."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        log.error("General error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An internal error occurred."));
    }
}
