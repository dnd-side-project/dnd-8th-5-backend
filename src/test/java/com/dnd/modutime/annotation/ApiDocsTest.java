package com.dnd.modutime.annotation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.annotation.AliasFor;
import org.springframework.restdocs.RestDocumentationExtension;

import java.lang.annotation.*;

/**
 * Spring REST Docs 생성 테스트
 *
 * @see AutoConfigureMockMvc
 * @see AutoConfigureRestDocs
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Tag("apiDocs")
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public @interface ApiDocsTest {
    /**
     * Specifies the controllers to test. This is an alias of {@link #controllers()} which
     * can be used for brevity if no other attributes are defined. See
     * {@link #controllers()} for details.
     *
     * @return the controllers to test
     * @see #controllers()
     */
    @AliasFor("controllers")
    Class<?>[] value() default {};

    /**
     * Specifies the controllers to test. May be left blank if all {@code @Controller}
     * beans should be added to the application context.
     *
     * @return the controllers to test
     * @see #value()
     */
    @AliasFor("value")
    Class<?>[] controllers() default {};
}
