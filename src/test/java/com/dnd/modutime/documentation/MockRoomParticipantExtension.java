package com.dnd.modutime.documentation;

import com.dnd.modutime.annotation.WithMockRoomParticipant;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

public class MockRoomParticipantExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return HandlerMethodArgumentResolver.class.isAssignableFrom(
                parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        var annotation = extensionContext.getRequiredTestMethod()
                .getAnnotation(WithMockRoomParticipant.class);
        if (annotation == null) {
            throw new IllegalStateException("@WithMockRoomParticipant 어노테이션을 찾을 수 없습니다.");
        }
        return new MockRoomParticipantResolver(
                annotation.type(),
                annotation.roomUuid(),
                annotation.participantName(),
                annotation.userId()
        );
    }
}
