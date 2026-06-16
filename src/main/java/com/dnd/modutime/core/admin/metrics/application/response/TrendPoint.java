package com.dnd.modutime.core.admin.metrics.application.response;

/**
 * 추이 그래프의 한 구간(일별 또는 월별) 데이터 포인트.
 *
 * <p>{@code label} 은 x축 라벨로, daily 는 {@code "M/D"}, monthly 는 {@code "M월"} 형식이다(KST 기준).
 * 나머지 값은 해당 구간의 신규 발생 수이며, 빈 구간도 {@code 0} 으로 채운다.</p>
 */
public record TrendPoint(
        String label,
        long rooms,
        long loggedIn,
        long anonymous,
        long participants
) {
}
