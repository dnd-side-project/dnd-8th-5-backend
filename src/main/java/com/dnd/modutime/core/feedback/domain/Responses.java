package com.dnd.modutime.core.feedback.domain;

import java.util.List;

/**
 * {@link ResponsePair} 목록을 감싸는 값 객체. JSON 배열로 직렬화되어 {@code responses} 컬럼에 저장된다.
 */
public record Responses(List<ResponsePair> values) {

    public Responses {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("responses는 최소 1개 이상이어야 합니다.");
        }
        values = List.copyOf(values);
    }
}
