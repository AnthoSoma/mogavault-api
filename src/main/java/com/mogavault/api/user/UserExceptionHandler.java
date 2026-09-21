package com.mogavault.api.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice(assignableTypes = UserController.class)
public class UserExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );

        problemDetail.setTitle("User conflict");
        problemDetail.setType(URI.create("https://mogavault.dev/errors/user-conflict"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("conflict", Map.of(
                "field", ex.getField(),
                "value", ex.getRejectedValue()
        ));

        return problemDetail;
    }
}