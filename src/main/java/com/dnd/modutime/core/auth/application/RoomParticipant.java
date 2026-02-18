package com.dnd.modutime.core.auth.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Guest/OAuth 통합 어노테이션.
 * Guest JWT 또는 OAuth2 인증 정보에서 {@link ParticipantInfo}를 추출하여 주입합니다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoomParticipant {
    /**
     * roomUuid를 담고 있는 path variable의 이름.
     */
    String roomPathVariable() default "roomUuid";
}
