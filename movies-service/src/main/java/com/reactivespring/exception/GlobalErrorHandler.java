package com.reactivespring.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(MoviesInfoClientException.class)
    public ResponseEntity<String> handleMoviesInfoClientException(MoviesInfoClientException ex) {
        log.error("MoviesInfoClientException caught at GlobalErrorHandler: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException caught at GlobalErrorHandler: {}", ex.getMessage());
        return ResponseEntity.status(500).body(ex.getMessage());
    }
}
