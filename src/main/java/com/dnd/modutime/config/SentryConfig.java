package com.dnd.modutime.config;

import com.dnd.modutime.exception.InvalidPasswordException;
import com.dnd.modutime.exception.NotFoundException;
import io.sentry.SentryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryConfig {

    @Bean
    public SentryOptions.BeforeSendCallback beforeSendCallback() {
        return (event, hint) -> {
            var ex = event.getThrowable();
            if (ex instanceof IllegalArgumentException
                    || ex instanceof NotFoundException
                    || ex instanceof InvalidPasswordException) {
                return null;
            }
            return event;
        };
    }
}
