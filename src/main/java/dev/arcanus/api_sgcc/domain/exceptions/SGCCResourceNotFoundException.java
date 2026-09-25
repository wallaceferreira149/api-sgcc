package dev.arcanus.api_sgcc.domain.exceptions;

import org.springframework.http.HttpStatus;

public class SGCCResourceNotFoundException extends SGCCException {

    public SGCCResourceNotFoundException(String title) {
        super(
            HttpStatus.NOT_FOUND,
            title,
            "Recurso não encontrado"
        );
    }

}
