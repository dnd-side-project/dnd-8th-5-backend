package com.dnd.modutime.core.admin.feedback.domain;

/**
 * 어드민이 트리아지 시 부여하는 처리 상태. DB/엔티티에는 대문자(name)로 저장하고,
 * API 입출력은 소문자({@code open}/{@code in_progress}/{@code resolved}/{@code closed})로 주고받는다.
 */
public enum TriageStatus {
    OPEN, IN_PROGRESS, RESOLVED, CLOSED;

    /**
     * API 응답에 노출되는 소문자 표현. ({@code IN_PROGRESS} → {@code in_progress})
     */
    public String getValue() {
        return name().toLowerCase();
    }

    /**
     * 대소문자 무관하게 파싱한다. null 은 (부분 수정 시 미변경 의미로) 그대로 통과시킨다.
     */
    public static TriageStatus from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 status 값입니다: " + value);
        }
    }
}
