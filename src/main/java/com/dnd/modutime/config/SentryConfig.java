package com.dnd.modutime.config;

import com.dnd.modutime.core.auth.oauth.facade.BadCredentialsException;
import com.dnd.modutime.exception.InvalidPasswordException;
import com.dnd.modutime.exception.NotFoundException;
import io.sentry.Sentry;
import io.sentry.SentryOptions;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Configuration
public class SentryConfig {

    /**
     * 4xx 클라이언트 에러로 간주해 Sentry 알림에서 제외할 예외 목록.
     * addIgnoredExceptionForType 만으로는 SentryExceptionResolver 가 캡처하는 예외를
     * 일관되게 차단하지 못해, BeforeSendCallback 에서도 동일 목록으로 한 번 더 거른다.
     */
    private static final List<Class<? extends Throwable>> IGNORED_EXCEPTIONS = List.of(
            IllegalArgumentException.class,
            NotFoundException.class,
            InvalidPasswordException.class,
            BadCredentialsException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            BindException.class,
            AccessDeniedException.class
    );

    @Bean
    public Sentry.OptionsConfiguration<SentryOptions> sentryOptionsConfiguration() {
        return options -> {
            IGNORED_EXCEPTIONS.forEach(options::addIgnoredExceptionForType);
            options.setBeforeSend((event, hint) -> {
                var ex = event.getThrowable();
                if (ex == null) {
                    return event;
                }
                if (isIgnored(ex)) {
                    return null;
                }
                return event;
            });
        };
    }

    private boolean isIgnored(Throwable ex) {
        return IGNORED_EXCEPTIONS.stream().anyMatch(type -> type.isInstance(ex));
    }
}
