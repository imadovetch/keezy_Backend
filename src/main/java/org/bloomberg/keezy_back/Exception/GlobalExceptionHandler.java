package org.bloomberg.keezy_back.Exception;

import org.bloomberg.keezy_back.DTO.ErrorResponseDTO;
import org.bloomberg.keezy_back.DTO.UnifiedResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<UnifiedResponse<Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        UnifiedResponse<Object> resp = new UnifiedResponse<>(
            java.util.Arrays.asList(ex.getMessage()),
            java.util.Collections.emptyList(),
            null,
            false
        );
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<UnifiedResponse<Object>> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        UnifiedResponse<Object> resp = new UnifiedResponse<>(
            java.util.Arrays.asList(ex.getMessage()),
            java.util.Collections.emptyList(),
            null,
            false
        );
        return new ResponseEntity<>(resp, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<UnifiedResponse<Object>> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        UnifiedResponse<Object> resp = new UnifiedResponse<>(
            java.util.Arrays.asList("Invalid email or password"),
            java.util.Collections.emptyList(),
            null,
            false
        );
        return new ResponseEntity<>(resp, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UnifiedResponse<Object>> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce((s1, s2) -> s1 + ", " + s2)
            .orElse("Validation error");

        UnifiedResponse<Object> resp = new UnifiedResponse<>(
            java.util.Arrays.asList(message),
            java.util.Collections.emptyList(),
            null,
            false
        );

        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }
}

