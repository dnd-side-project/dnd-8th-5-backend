package com.dnd.modutime.core.auth.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Guest JWT 토큰에서 roomUuid와 participantName을 추출하여 {@link GuestInfo}로 주입하는 어노테이션.
 * 토큰의 roomUuid와 요청 경로의 roomUuid가 일치하는지 검증합니다.
 *
 * @deprecated {@link RoomParticipant}로 대체되었습니다. Guest/OAuth 통합 지원.
 */
@Deprecated
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuestParticipant {
    /**
     * roomUuid를 담고 있는 path variable의 이름
     */
    String roomPathVariable();
}
