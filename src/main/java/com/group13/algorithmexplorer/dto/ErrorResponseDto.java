package com.group13.algorithmexplorer.dto;

/**
 * Structured HTTP error response payload without using {@code java.util.Map}.
 */
public class ErrorResponseDto {

    private int status;
    private String error;
    private String message;

    public ErrorResponseDto() {
    }

    public ErrorResponseDto(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
