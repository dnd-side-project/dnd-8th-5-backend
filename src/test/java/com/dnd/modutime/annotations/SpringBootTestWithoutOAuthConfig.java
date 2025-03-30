package com.dnd.modutime.annotations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;

import java.lang.annotation.*;

/**
 * {@link SpringBootTest}를 상속받아 OAuth 관련 설정을 제외한 테스트용 어노테이션입니다.
 * 어노테이션을 사용하고도 @MockBean을 사용하여 관련된 빈을 등록해야 하는 경우가 있습니다.
 *
 * @see com.dnd.modutime.ModutimeApplicationTests
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@SpringBootTest
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class,
})
public @interface SpringBootTestWithoutOAuthConfig {

    /**
     * The type of web environment to create when applicable. Defaults to
     * {@link SpringBootTest.WebEnvironment#MOCK}.
     *
     * @return the type of web environment
     */
    SpringBootTest.WebEnvironment webEnvironment() default SpringBootTest.WebEnvironment.MOCK;

    /**
     * An enumeration web environment modes.
     */
    enum WebEnvironment {

        /**
         * Creates a {@link WebApplicationContext} with a mock servlet environment if
         * servlet APIs are on the classpath, a {@link ReactiveWebApplicationContext} if
         * Spring WebFlux is on the classpath or a regular {@link ApplicationContext}
         * otherwise.
         */
        MOCK(false),

        /**
         * Creates a web application context (reactive or servlet based) and sets a
         * {@code server.port=0} {@link Environment} property (which usually triggers
         * listening on a random port). Often used in conjunction with a
         * {@link LocalServerPort @LocalServerPort} injected field on the test.
         */
        RANDOM_PORT(true),

        /**
         * Creates a (reactive) web application context without defining any
         * {@code server.port=0} {@link Environment} property.
         */
        DEFINED_PORT(true),

        /**
         * Creates an {@link ApplicationContext} and sets
         * {@link SpringApplication#setWebApplicationType(WebApplicationType)} to
         * {@link WebApplicationType#NONE}.
         */
        NONE(false);

        private final boolean embedded;

        WebEnvironment(boolean embedded) {
            this.embedded = embedded;
        }

        /**
         * Return if the environment uses an {@link ServletWebServerApplicationContext}.
         *
         * @return if an {@link ServletWebServerApplicationContext} is used.
         */
        public boolean isEmbedded() {
            return this.embedded;
        }

    }
}
