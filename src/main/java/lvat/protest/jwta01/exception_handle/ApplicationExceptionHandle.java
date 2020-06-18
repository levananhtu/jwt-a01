package lvat.protest.jwta01.exception_handle;

import lvat.protest.jwta01.payload.ArgumentsErrorPayload;
import lvat.protest.jwta01.payload.MessagePayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

public class ApplicationExceptionHandle {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(new ArgumentsErrorPayload(HttpStatus.BAD_REQUEST.value(), errors),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsExceptions(BadCredentialsException ex) {
        return new ResponseEntity<>(new MessagePayload("Credential is not valid"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAuthenticationExceptions(AccessDeniedException ex) {
        return new ResponseEntity<>(new MessagePayload("Full authentication is required "),
                HttpStatus.BAD_REQUEST);
    }

}
