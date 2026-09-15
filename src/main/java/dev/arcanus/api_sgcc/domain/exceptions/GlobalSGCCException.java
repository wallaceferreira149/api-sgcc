package dev.arcanus.api_sgcc.domain.exceptions;

import dev.arcanus.api_sgcc.infra.config.exception.SGCCException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class GlobalSGCCException extends SGCCException {

    public ProblemDetail toProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Problema interno no servidor do SGCC!");
        return problemDetail;
    }
}
