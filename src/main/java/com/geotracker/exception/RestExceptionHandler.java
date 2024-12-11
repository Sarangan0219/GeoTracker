package com.geotracker.exception;

import com.geotracker.model.view.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("RESOURCE_NOT_FOUND")
                .errorMessage(ex.getMessage())
                .details("Resource not found")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("UNAUTHORIZED_ACCESS")
                .errorMessage(ex.getMessage())
                .details("Unauthorized access")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("BAD_REQUEST")
                .errorMessage(ex.getMessage())
                .details("Bad request")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .errorMessage(ex.getMessage())
                .details("Validation error")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .errorMessage("An unexpected error occurred")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
