package dev.arcanus.api_sgcc.domain.exceptions;

import org.springframework.http.HttpStatus;

public class SGCCResourceAlreadyExists extends SGCCException {

    public SGCCResourceAlreadyExists(String detail) {
        super(
            HttpStatus.CONFLICT,
            "Recurso já cadastrado",
            detail
        );
    }

}
