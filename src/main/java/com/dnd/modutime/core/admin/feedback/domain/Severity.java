package com.dnd.modutime.core.admin.feedback.domain;

/**
 * 어드민이 트리아지 시 부여하는 심각도. DB/엔티티에는 대문자(name)로 저장하고,
 * API 입출력은 소문자({@code low}/{@code medium}/{@code high}/{@code critical})로 주고받는다.
 */
public enum Severity {
    LOW, MEDIUM, HIGH, CRITICAL;

    /**
     * API 응답에 노출되는 소문자 표현.
     */
    public String getValue() {
        return name().toLowerCase();
    }

    /**
     * 대소문자 무관하게 파싱한다. null 은 (부분 수정 시 미변경 의미로) 그대로 통과시킨다.
     */
    public static Severity from(String value) {
        if (value == null) {
            return null;
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 severity 값입니다: " + value);
        }
    }
}
