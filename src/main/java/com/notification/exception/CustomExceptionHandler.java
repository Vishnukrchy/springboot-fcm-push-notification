package com.notification.exception;

import com.notification.model.ApiResponse;
import com.notification.model.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotificationException.class)
    public final ResponseEntity<ApiResponse> handleNotificationException(NotificationException ex, WebRequest request) {
        log.error("Notification error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
            new ApiResponse(false, ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TimeoutException.class)
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ApiResponse handleTimeoutException(TimeoutException ex, WebRequest request) {
        log.error("Request timeout: {}", ex.getMessage(), ex);
        return new ApiResponse(false, "Request timeout. Please try again later.");
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(), 
                "An unexpected error occurred: " + ex.getMessage(), 
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
