package dev.arcanus.api_sgcc.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class GlobalSGCCException extends RuntimeException {

    public ProblemDetail toProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("SGCC Internal Server Error");
        return problemDetail;
    }
}
