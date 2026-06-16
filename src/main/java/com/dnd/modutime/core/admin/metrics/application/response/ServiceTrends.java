package com.dnd.modutime.core.admin.metrics.application.response;

import java.util.List;

/**
 * 어드민 대시보드 하단 추이 그래프 3종(방 생성/사용자 가입/참여자)에 쓰이는 일별·월별 추이.
 *
 * <p>{@code daily} 는 정확히 30개(최근 30일), {@code monthly} 는 정확히 12개(최근 12개월)이며,
 * 배열 순서는 오래된 구간 → 최신 구간이다.</p>
 */
public record ServiceTrends(
        List<TrendPoint> daily,
        List<TrendPoint> monthly
) {
}
