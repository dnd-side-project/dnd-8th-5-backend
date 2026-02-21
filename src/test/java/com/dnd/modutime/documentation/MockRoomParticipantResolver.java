package com.dnd.modutime.documentation;

import com.dnd.modutime.core.auth.application.ParticipantInfo;
import com.dnd.modutime.core.auth.application.ParticipantType;
import com.dnd.modutime.core.auth.application.RoomParticipant;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MockRoomParticipantResolver implements HandlerMethodArgumentResolver {

    private final ParticipantType type;
    private final String roomUuid;
    private final String participantName;
    private final Long userId;

    public MockRoomParticipantResolver(
            ParticipantType type,
            String roomUuid,
            String participantName,
            long userId
    ) {
        this.type = type;
        this.roomUuid = roomUuid;
        this.participantName = participantName;
        this.userId = userId == -1 ? null : userId;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RoomParticipant.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Class<?> parameterType = parameter.getParameterType();

        if (ParticipantInfo.class.isAssignableFrom(parameterType)) {
            return new ParticipantInfo(type, roomUuid, participantName, userId);
        }

        if (String.class.isAssignableFrom(parameterType)) {
            return participantName;
        }

        throw new IllegalArgumentException(
                "@RoomParticipant 파라미터 타입은 ParticipantInfo 또는 String이어야 합니다. 현재 타입: "
                        + parameterType.getName()
        );
    }
}
