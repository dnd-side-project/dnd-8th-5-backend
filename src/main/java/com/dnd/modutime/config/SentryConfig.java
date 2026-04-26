package com.dnd.modutime.config;

import com.dnd.modutime.exception.InvalidPasswordException;
import com.dnd.modutime.exception.NotFoundException;
import io.sentry.SentryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Configuration
public class SentryConfig {

    // application-sentry.yaml 의 ignored-exceptions-for-type 만으로는
    // SentryExceptionResolver 가 캡처하는 4xx 예외를 일관되게 차단하지 못해,
    // BeforeSendCallback 에서 한 번 더 명시적으로 거른다.
    @Bean
    public SentryOptions.BeforeSendCallback beforeSendCallback() {
        return (event, hint) -> {
            var ex = event.getThrowable();
            if (ex == null) {
                return event;
            }
            if (isClientError(ex)) {
                return null;
            }
            return event;
        };
    }

    private boolean isClientError(Throwable ex) {
        return ex instanceof IllegalArgumentException
                || ex instanceof NotFoundException
                || ex instanceof InvalidPasswordException
                || ex instanceof MethodArgumentNotValidException
                || ex instanceof HttpMessageNotReadableException
                || ex instanceof BindException
                || ex instanceof AccessDeniedException;
    }
}
