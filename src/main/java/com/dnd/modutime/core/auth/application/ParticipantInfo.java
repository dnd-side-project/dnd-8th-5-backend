package com.dnd.modutime.core.auth.application;

/**
 * Guest/OAuth 통합 참여자 정보.
 * Guest: {@code new ParticipantInfo(GUEST, roomUuid, participantName, null)}
 * OAuth: {@code new ParticipantInfo(OAUTH, roomUuid, participantName, userId)}
 *
 * roomUuid는 두 경우 모두 path variable에서 추출됩니다.
 */
public record ParticipantInfo(
        ParticipantType type,
        String roomUuid,
        String participantName,
        Long userId
) {

    public boolean isGuest() {
        return type == ParticipantType.GUEST;
    }

    public boolean isOAuth() {
        return type == ParticipantType.OAUTH;
    }
}
