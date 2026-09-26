package dev.arcanus.api_sgcc.domain.exceptions;

import org.springframework.http.HttpStatus;

public class SGCCInvalidRequestException extends SGCCException {

    public SGCCInvalidRequestException(String detail) {
        super(
            HttpStatus.BAD_REQUEST,
            "Dados inválidos",
            detail
        );
    }

}
