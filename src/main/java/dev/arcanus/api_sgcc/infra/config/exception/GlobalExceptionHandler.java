package dev.arcanus.api_sgcc.infra.config.exception;

import dev.arcanus.api_sgcc.domain.exceptions.SGCCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String ERRORS_URI = "https://sgcc.arcanus.dev/errors/";

    @ExceptionHandler(SGCCException.class)
    public ResponseEntity<ProblemDetail> handleSGCCException(SGCCException ex) {
        log.error("Exceção de negócio tratada: {}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatus()).body(ex.toProblemDetail());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Violação de integridade de dados: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "A operação conflita com um registro já existente"
        );
        problemDetail.setTitle("Conflito de dados");
        problemDetail.setType(URI.create(ERRORS_URI + "conflict"));
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }
}
