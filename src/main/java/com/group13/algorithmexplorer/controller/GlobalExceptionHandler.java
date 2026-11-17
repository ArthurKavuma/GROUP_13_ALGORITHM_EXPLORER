package com.group13.algorithmexplorer.controller;

import com.group13.algorithmexplorer.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates validation and parsing exceptions into structured HTTP 400 JSON responses
 * without using {@code java.util.Map}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Malformed JSON request body. Please check that all numeric inputs are valid integers."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.getMessage() != null ? ex.getMessage() : "Unexpected server error."
        );
    }

    private static ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponseDto body = new ErrorResponseDto(status.value(), status.getReasonPhrase(), message);
        return ResponseEntity.status(status).body(body);
    }
}
