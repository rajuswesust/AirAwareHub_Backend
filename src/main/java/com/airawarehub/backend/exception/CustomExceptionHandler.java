package com.airawarehub.backend.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;

@RestControllerAdvice
@Order(100) // This will make it the last exception handler in the chain
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {

//        ProblemDetail errorDetail = null;
//        if(exception instanceof BadCredentialsException) {
//            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
//            errorDetail.setProperty("access_denied_reason", "username or password not correct");
//        }
//        else if(exception instanceof AccessDeniedException) {
//            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
//            errorDetail.setProperty("access_denied_reason", "You are not allowed to access");
//        }
//
//        else if(exception instanceof SignatureException) {
//            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
//            errorDetail.setProperty("access_denied_reason", "invalid jwt signature");
//        }
//
//        else if(exception instanceof MalformedJwtException) {
//            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
//            errorDetail.setProperty("access_denied_reason", "malformed jwt signature");
//        }
//
//        else if(exception instanceof ExpiredJwtException) {
//            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
//            errorDetail.setProperty("access_denied_reason", "Jwt token expired!");
//        }
//        else if(exception instanceof UnauthorizedException) {
//            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
//            errorDetail.setProperty("access_denied_reason", "You are not allowed to access");
//        }
//        else {
//            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
//            errorDetail.setProperty("message", exception.getMessage());
//        }
//        return  errorDetail;
        return null;
    }
}
