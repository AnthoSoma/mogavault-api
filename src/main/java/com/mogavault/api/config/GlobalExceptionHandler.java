package com.mogavault.api.config;

import com.mogavault.api.common.exception.ConflictException;
import com.mogavault.api.common.exception.ResourceNotFoundException;
import com.mogavault.api.common.exception.ValidationErrorItem;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Set<String> IGNORED_ATTRIBUTES = Set.of("message", "groups", "payload");

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setTitle("Invalid Request");
        problemDetail.setDetail("Data validation has failed");
        problemDetail.setType(URI.create("https://mogavault.dev/errors/validation-error"));
        problemDetail.setProperty("timestamp", Instant.now());

        Map<String, ValidationErrorItem> invalidFields = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            Map<String, Object> params = extractConstraintAttributes(fieldError);
            invalidFields.put(
                    fieldError.getField(),
                    new ValidationErrorItem(fieldError.getDefaultMessage(), params)
            );
        }

        problemDetail.setProperty("errors", invalidFields);

        return ResponseEntity.status(status).body(problemDetail);
    }

    private Map<String, Object> extractConstraintAttributes(FieldError fieldError) {
        try {
            ConstraintViolation<?> violation = fieldError.unwrap(ConstraintViolation.class);
            Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();

            Map<String, Object> filteredParams = new HashMap<>();
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                if (!IGNORED_ATTRIBUTES.contains(entry.getKey())) {
                    filteredParams.put(entry.getKey(), entry.getValue());
                }
            }
            return filteredParams;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessageKey()
        );
        problemDetail.setTitle("Resource not found");
        problemDetail.setType(URI.create("https://mogavault.dev/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());

        // Ajout des paramètres dynamiques dans le payload d'erreur
        if (!ex.getMessageParams().isEmpty()) {
            problemDetail.setProperty("params", ex.getMessageParams());
        }

        return problemDetail;
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Resource conflict");
        problemDetail.setType(URI.create("https://mogavault.dev/errors/conflict"));
        problemDetail.setProperty("timestamp", Instant.now());

        Map<String, Object> details = ex.getDetails();
        if (!details.isEmpty()) {
            problemDetail.setProperty("details", details);
        }

        return problemDetail;
    }
}