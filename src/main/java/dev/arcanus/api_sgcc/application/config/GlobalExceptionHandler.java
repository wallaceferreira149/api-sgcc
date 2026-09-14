package dev.arcanus.api_sgcc.application.config;

import dev.arcanus.api_sgcc.domain.exceptions.GlobalSGCCException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalSGCCException.class)
    public ProblemDetail handleGlobalSGCCException(GlobalSGCCException ex) {
        // TODO: Colocar um log de erro
        return ex.toProblemDetail();
    }
}
