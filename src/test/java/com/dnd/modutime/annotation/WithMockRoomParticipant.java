package com.dnd.modutime.annotation;

import com.dnd.modutime.core.auth.application.ParticipantType;
import com.dnd.modutime.documentation.MockRoomParticipantExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MockRoomParticipantExtension.class)
public @interface WithMockRoomParticipant {
    ParticipantType type() default ParticipantType.GUEST;
    String roomUuid() default "test-room-uuid";
    String participantName() default "동호";
    long userId() default -1;
}
