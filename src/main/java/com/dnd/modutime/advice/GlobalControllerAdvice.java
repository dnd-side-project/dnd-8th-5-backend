package com.dnd.modutime.advice;

import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.common.ErrorResponse;
import com.dnd.modutime.exception.AuthenticationException;
import com.dnd.modutime.exception.InvalidPasswordException;
import com.dnd.modutime.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.BindException;

@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception) {
        return build(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, exception);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorizedException(InvalidPasswordException exception) {
        return build(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, exception);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, ErrorCode.MT404, exception);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.MT400, exception);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handle(org.springframework.validation.BindException e) {
        var fieldErrors = e.getBindingResult().getFieldErrors();
        var message = fieldErrors.isEmpty() ? null : fieldErrors.get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(ErrorCode.MT400, resolveMessage(message, ErrorCode.MT400),
                        HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerError(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(ErrorCode.MT500, ErrorCode.MT500.getDescription(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, ErrorCode errorCode, Exception exception) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.from(errorCode, resolveMessage(exception.getMessage(), errorCode), status.value()));
    }

    private String resolveMessage(String message, ErrorCode fallback) {
        if (message == null || message.isBlank()) {
            return fallback.getDescription();
        }
        return message;
    }
}
