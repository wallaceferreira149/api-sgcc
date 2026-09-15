package dev.arcanus.api_sgcc.config;

import dev.arcanus.api_sgcc.application.exceptions.GlobalSGCCException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GlobalSGCCException.class)
    public ProblemDetail handleGlobalSGCCException(GlobalSGCCException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Erro inesperado no SGCC - contacte o adminstrador!" + ex.getMessage()
        );
    }
}
