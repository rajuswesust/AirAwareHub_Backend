package com.airawarehub.backend.exception;

import com.airawarehub.backend.payload.SimpleResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseStatusExceptionCustom extends RuntimeException{
    private SimpleResponse simpleResponse;

    private String message;

    public ResponseStatusExceptionCustom(SimpleResponse simpleResponse) {
        super();
        this.simpleResponse = simpleResponse;
    }

    public ResponseStatusExceptionCustom(String message) {
        super(message);
        this.message = message;
    }
}
