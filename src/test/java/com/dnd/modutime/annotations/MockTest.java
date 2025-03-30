package com.dnd.modutime.annotations;

import org.mockito.junit.jupiter.MockitoSettings;

import java.lang.annotation.*;

/**
 * Mocking 테스트용 애노테이션
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MockitoSettings
public @interface MockTest {
}
