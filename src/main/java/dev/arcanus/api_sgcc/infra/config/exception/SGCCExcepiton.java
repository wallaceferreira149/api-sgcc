package dev.arcanus.api_sgcc.infra.config.exception;

public abstract class SGCCException extends RuntimeException {

    private final HttpStatus status;
    private final String title;
    private final String detail;

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

    private String slugify(String text) {
        return text.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-");
    }

    public HttpStatus getStatus() {
        return status;
    }
}
