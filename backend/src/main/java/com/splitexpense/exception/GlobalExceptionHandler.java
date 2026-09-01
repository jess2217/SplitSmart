package com.splitexpense.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================
    // RESPONSE STATUS EXCEPTION
    // =========================

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>>
    handleResponseStatusException(
            ResponseStatusException exception) {

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(
                        Map.of(
                                "message",
                                exception.getReason() != null
                                        ? exception.getReason()
                                        : "Request failed."
                        )
                );
    }


    // =========================
    // INVALID EXPENSE
    // =========================

    @ExceptionHandler(InvalidExpenseException.class)
    public ResponseEntity<Map<String, String>>
    handleInvalidExpenseException(
            InvalidExpenseException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of(
                                "message",
                                exception.getMessage()
                        )
                );
    }


    // =========================
    // INVALID SPLIT
    // =========================

    @ExceptionHandler(InvalidSplitException.class)
    public ResponseEntity<Map<String, String>>
    handleInvalidSplitException(
            InvalidSplitException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of(
                                "message",
                                exception.getMessage()
                        )
                );
    }


    // =========================
    // ILLEGAL ARGUMENT
    // =========================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>>
    handleIllegalArgumentException(
            IllegalArgumentException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of(
                                "message",
                                exception.getMessage()
                        )
                );
    }
}