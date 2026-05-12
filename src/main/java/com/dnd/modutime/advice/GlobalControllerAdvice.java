package com.dnd.modutime.advice;

import com.dnd.modutime.core.auth.oauth.exception.KakaoApiException;
import com.dnd.modutime.core.auth.oauth.exception.KakaoEmailNotProvidedException;
import com.dnd.modutime.core.common.ErrorCode;
import com.dnd.modutime.core.common.ErrorResponse;
import com.dnd.modutime.exception.AuthenticationException;
import com.dnd.modutime.exception.InvalidPasswordException;
import com.dnd.modutime.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception) {
        return build(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, exception);
    }

    @ExceptionHandler(KakaoEmailNotProvidedException.class)
    public ResponseEntity<ErrorResponse> handleKakaoEmailNotProvided(KakaoEmailNotProvidedException exception) {
        return build(HttpStatus.FORBIDDEN, ErrorCode.KAKAO_EMAIL_NOT_PROVIDED, exception);
    }

    @ExceptionHandler(KakaoApiException.class)
    public ResponseEntity<ErrorResponse> handleKakaoApiException(KakaoApiException exception) {
        log.error("카카오 API 호출 오류: {}", exception.getMessage(), exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.KAKAO_API_ERROR, exception);
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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        return handleBindException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(ErrorCode.MT400, ErrorCode.MT400.getDescription(),
                        HttpStatus.BAD_REQUEST.value()));
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         WebRequest request) {
        var fieldErrors = ex.getBindingResult().getFieldErrors();
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
