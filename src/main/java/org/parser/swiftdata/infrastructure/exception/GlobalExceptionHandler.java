package org.parser.swiftdata.infrastructure.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parser.swiftdata.infrastructure.error.ErrorWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorWrapper> handleGeneralException(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "An error has occurred.";
        ErrorWrapper errorWrapper = new ErrorWrapper(message, status, request.getRequestURI(), status);
        return new ResponseEntity<>(errorWrapper, status);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorWrapper> handleNoResourceException(
            NoResourceFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Invalid call URL";
        ErrorWrapper errorWrapper = new ErrorWrapper(message, status, request.getRequestURI(), status);
        logError(ex.getMessage(), request.getRequestURI(), ex);
        return new ResponseEntity<>(errorWrapper, status);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorWrapper> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Argument type mismatch";
        ErrorWrapper errorWrapper = new ErrorWrapper(message, status, request.getRequestURI(), status);
        logError(ex.getMessage(), request.getRequestURI(), ex);
        return new ResponseEntity<>(errorWrapper, status);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorWrapper> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = String.format("'%s' parameter is missing", ex.getParameterName());
        ErrorWrapper errorWrapper = new ErrorWrapper(message, status, request.getRequestURI(), status);
        logError(ex.getMessage(), request.getRequestURI(), ex);
        return new ResponseEntity<>(errorWrapper, status);
    }

    @ExceptionHandler(InvalidCallException.class)
    public ResponseEntity<ErrorWrapper> handleInvalidCallException(
            InvalidCallException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Invalid call";
        ErrorWrapper errorWrapper = new ErrorWrapper(message, status, request.getRequestURI(), status);
        logError(ex.getMessage(), request.getRequestURI(), ex);
        return new ResponseEntity<>(errorWrapper, status);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorWrapper> handleJsonProcessingException(
            JsonProcessingException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Json processing error";
        ErrorWrapper errorWrapper = new ErrorWrapper(message, status, request.getRequestURI(), status);
        logError(ex.getMessage(), request.getRequestURI(), ex);
        return new ResponseEntity<>(errorWrapper, status);
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ErrorWrapper> handleClassCastException(ClassCastException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Class cast exception";
        ErrorWrapper errorWrapper = new ErrorWrapper(message, status, request.getRequestURI(), status);
        logError(ex.getMessage(), request.getRequestURI(), ex);
        return new ResponseEntity<>(errorWrapper, status);
    }

    private <T extends Exception> void logError(String message, String uri, T e) {
        log.error("{} at uri: {}; Details: {}", message, uri, e.getMessage());
    }
}
