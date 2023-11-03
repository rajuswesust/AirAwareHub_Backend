package com.airawarehub.backend.exception;

import com.airawarehub.backend.payload.SimpleResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessDeniedExceptionCustom extends RuntimeException {
    private SimpleResponse simpleResponse;

    private String message;

    public AccessDeniedExceptionCustom(SimpleResponse simpleResponse) {
        super();
        this.simpleResponse = simpleResponse;
    }

    public AccessDeniedExceptionCustom(String message) {
        super(message);
        this.message = message;
    }
}
