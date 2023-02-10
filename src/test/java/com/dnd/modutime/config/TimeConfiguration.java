package com.dnd.modutime.config;

import com.dnd.modutime.domain.FakeTimeProvider;
import com.dnd.modutime.util.TimeProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TimeConfiguration {

    @Bean
    public TimeProvider timeProvider() {
        return new FakeTimeProvider();
    }
}
