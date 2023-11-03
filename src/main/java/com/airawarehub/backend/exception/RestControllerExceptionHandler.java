package com.airawarehub.backend.exception;


import com.airawarehub.backend.payload.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

@RestControllerAdvice
@Order(99)
public class RestControllerExceptionHandler {

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public ResponseEntity<SimpleResponse> resolveException(ApiException exception) {
        String message = exception.getMessage();

        //important
        HttpStatus status = exception.getStatus();

        SimpleResponse simpleResponse = new SimpleResponse(status.value(), message);
        return new ResponseEntity<>(simpleResponse, status);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<SimpleResponse> resolveException(UnauthorizedException exception) {

        SimpleResponse simpleResponse = exception.getSimpleResponse();

        return new ResponseEntity<>(simpleResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ResponseEntity<?> handleBadRequestException(BadRequestException exception) {
        ApiResponse apiResponse = exception.getApiResponse();
        AuthenticationResponse authenticationResponse = exception.getAuthenticationResponse();
        SimpleResponse simpleResponse = exception.getSimpleResponse();

        if (apiResponse != null) {
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } else if (authenticationResponse != null) {
            return new ResponseEntity<>(authenticationResponse, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(simpleResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> resolveException(ResourceNotFoundException exception) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(), "resource not found");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(AccessDeniedExceptionCustom.class)
    @ResponseBody
    public ResponseEntity<SimpleResponse> resolveException(AccessDeniedExceptionCustom exception) {
        SimpleResponse simpleResponse = exception.getSimpleResponse();
        return new ResponseEntity<>(simpleResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> resolveException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> messages = new ArrayList<>(fieldErrors.size());
        for (FieldError error : fieldErrors) {
            messages.add(error.getField() + " - " + error.getDefaultMessage());
        }
        return new ResponseEntity<>(new ExceptionResponse(messages, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> resolveException(MethodArgumentTypeMismatchException ex) {
        String message = "Parameter '" + ex.getParameter().getParameterName() + "' must be '"
                + Objects.requireNonNull(ex.getRequiredType()).getSimpleName() + "'";
        List<String> messages = new ArrayList<>(1);
        messages.add(message);
        return new ResponseEntity<>(new ExceptionResponse(messages, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> resolveException(HttpRequestMethodNotSupportedException ex) {
        String message = "Request method '" + ex.getMethod() + "' not supported. List of all supported methods - "
                + ex.getSupportedHttpMethods();
        List<String> messages = new ArrayList<>(1);
        messages.add(message);

        return new ResponseEntity<>(new ExceptionResponse(messages, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                HttpStatus.METHOD_NOT_ALLOWED.value()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> resolveException(HttpMessageNotReadableException ex) {
        String message = "Please provide Request Body in valid JSON format";
        List<String> messages = new ArrayList<>(1);
        messages.add(message);
        return new ResponseEntity<>(new ExceptionResponse(messages, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> resolveException(ServerException exception) {
        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put("message", exception.getMessage());
        return new ResponseEntity<>(responseMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> resolveException(ConstraintViolationException ex) {
        List<String> individualViolationMessages = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
            individualViolationMessages.add(constraintViolation.getMessage());
        }

        return new ResponseEntity<>(new ExceptionResponse(individualViolationMessages, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }
}

