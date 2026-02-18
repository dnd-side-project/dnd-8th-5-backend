package com.dnd.modutime.core.auth.application;

/**
 * @deprecated {@link ParticipantInfo}로 대체되었습니다. Guest/OAuth 통합 지원.
 */
@Deprecated
public record GuestInfo(String roomUuid, String participantName) {
}
