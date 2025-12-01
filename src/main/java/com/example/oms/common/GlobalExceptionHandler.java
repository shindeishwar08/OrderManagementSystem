package com.example.oms.common;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // For @Valid errors
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.oms.common.exception.EmailAlreadyExistsException;
import com.example.oms.common.exception.InvalidStateException;
import com.example.oms.common.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Resource Not Found (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // 2. Handle Invalid State (400) - e.g. Cancelling a delivered order
    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ApiError> handleInvalidState(InvalidStateException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 3. Handle Validation Errors (400) - e.g. Empty Email
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
        // Combine all validation errors into one string
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    // 4. Handle Access Denied (403) - From Spring Security
    // (Note: You might need to import org.springframework.security.access.AccessDeniedException)
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class) 
    public ResponseEntity<ApiError> handleAccessDenied(Exception e) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access Denied: You do not have permission");
    }

    // 5. Catch-All (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
    // 6. Email Already Exists(409-CONFLICT)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> emailAlreadyExists(EmailAlreadyExistsException e){
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message) {
        ApiError error = ApiError.builder()
                .errorCode(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, status);
    }
}