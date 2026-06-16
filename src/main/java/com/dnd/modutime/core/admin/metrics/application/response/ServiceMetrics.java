package com.dnd.modutime.core.admin.metrics.application.response;

/**
 * 어드민 대시보드 상단 StatCard 4종에 쓰이는 현재 시점 요약 지표.
 *
 * <p>모든 값은 음수가 아닌 정수이며, 데이터가 없으면 {@code 0} 이다(null 금지).
 * 프론트가 로그인/비로그인 비율을 계산하므로 {@code totalUsers == loggedInUsers + anonymousUsers} 불변식을 보장한다.
 * 응답 키는 단일 단어 camelCase 로 그대로 직렬화된다.</p>
 */
public record ServiceMetrics(
        long totalUsers,
        long loggedInUsers,
        long anonymousUsers,
        long totalRooms,
        long activeRooms,
        long totalParticipants,
        long newRoomsLast7d
) {

    public static ServiceMetrics of(long loggedInUsers,
                                    long anonymousUsers,
                                    long totalRooms,
                                    long activeRooms,
                                    long totalParticipants,
                                    long newRoomsLast7d) {
        return new ServiceMetrics(
                loggedInUsers + anonymousUsers,
                loggedInUsers,
                anonymousUsers,
                totalRooms,
                activeRooms,
                totalParticipants,
                newRoomsLast7d
        );
    }
}
