package org.trainee.userservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int statusCode;
    private String error;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, HttpStatus status) {
        this.message = message;
        this.statusCode = status.value();
        this.error = status.getReasonPhrase();
        this.timestamp = LocalDateTime.now();
    }
}
