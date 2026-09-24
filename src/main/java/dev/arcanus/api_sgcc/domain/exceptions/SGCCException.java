package dev.arcanus.api_sgcc.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.time.Instant;

public abstract class SGCCException extends RuntimeException {

    private HttpStatus status;
    private String title;
    private String detail;

    protected SGCCException(HttpStatus status, String title, String detail) {
        super(detail);
        this.status = status;
        this.title = title;
        this.detail = detail;
    }

    protected SGCCException(HttpStatus status, String title, String detail, Throwable cause) {
        super(detail, cause);
        this.status = status;
        this.title = title;
        this.detail = detail;
    }

    public ProblemDetail toProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create("https://sgcc.arcanus.dev/errors/" + slugify(title)));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    protected SGCCException() {}

    private String slugify(String text) {
        return text.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-");
    }

    public HttpStatus getStatus() {
        return status;
    }
}
