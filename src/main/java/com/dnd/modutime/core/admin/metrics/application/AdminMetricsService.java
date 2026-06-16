package com.dnd.modutime.core.admin.metrics.application;

import com.dnd.modutime.core.admin.metrics.application.response.ServiceMetrics;
import com.dnd.modutime.core.admin.metrics.application.response.ServiceTrends;
import com.dnd.modutime.core.admin.metrics.application.response.TrendPoint;
import com.dnd.modutime.core.participant.domain.ParticipantQueryRepository;
import com.dnd.modutime.core.room.repository.RoomRepository;
import com.dnd.modutime.core.timeblock.repository.TimeBlockRepository;
import com.dnd.modutime.core.user.UserRepository;
import com.dnd.modutime.util.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 어드민 대시보드 메트릭(요약 지표 + 추이) 유스케이스.
 *
 * <p>집계는 기존 도메인 레포지토리를 재사용해 읽기 전용으로 수행한다. 추이는 DB 방언에 의존하지 않도록
 * createdAt 원본을 조회한 뒤 {@link TimeProvider} 기준 KST 로 일별/월별 버킷에 직접 분배한다.</p>
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminMetricsService {

    private static final int ACTIVE_WINDOW_DAYS = 7;
    private static final int DAILY_POINTS = 30;
    private static final int MONTHLY_POINTS = 12;

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final TimeBlockRepository timeBlockRepository;
    private final ParticipantQueryRepository participantQueryRepository;
    private final TimeProvider timeProvider;

    /**
     * 현재 시점 요약 지표. totalUsers 는 loggedIn + anonymous 합으로 산출해 불변식을 보장한다.
     */
    public ServiceMetrics getMetrics() {
        var now = timeProvider.getCurrentLocalDateTime();
        var sevenDaysAgo = now.minusDays(ACTIVE_WINDOW_DAYS);

        long loggedInUsers = userRepository.count();
        long anonymousUsers = participantQueryRepository.countByUserIdIsNull();
        long totalRooms = roomRepository.count();
        long activeRooms = timeBlockRepository.countActiveRoomsSince(sevenDaysAgo);
        long totalParticipants = participantQueryRepository.countAll();
        long newRoomsLast7d = roomRepository.countByCreatedAtAfter(sevenDaysAgo);

        return ServiceMetrics.of(
                loggedInUsers, anonymousUsers, totalRooms, activeRooms, totalParticipants, newRoomsLast7d);
    }

    /**
     * 일별(최근 30일) · 월별(최근 12개월) 추이. 각 소스의 createdAt 을 KST 날짜/월 버킷에 분배한다.
     */
    public ServiceTrends getTrends() {
        var today = timeProvider.getCurrentLocalDateTime().toLocalDate();
        var currentMonth = YearMonth.from(today);

        // 12개월 전 1일 0시 — daily(30일)·monthly(12개월) 양쪽을 모두 덮는 가장 이른 기준 시각.
        var from = currentMonth.minusMonths(MONTHLY_POINTS - 1L).atDay(1).atStartOfDay();

        var roomCreatedAts = roomRepository.findCreatedAtsAfter(from);
        var userCreatedAts = userRepository.findCreatedAtsAfter(from);
        var guestCreatedAts = participantQueryRepository.findGuestCreatedAtsAfter(from);
        var participantCreatedAts = participantQueryRepository.findCreatedAtsAfter(from);

        var daily = buildDaily(today, roomCreatedAts, userCreatedAts, guestCreatedAts, participantCreatedAts);
        var monthly = buildMonthly(currentMonth, roomCreatedAts, userCreatedAts, guestCreatedAts, participantCreatedAts);
        return new ServiceTrends(daily, monthly);
    }

    private List<TrendPoint> buildDaily(LocalDate today,
                                        List<LocalDateTime> rooms,
                                        List<LocalDateTime> users,
                                        List<LocalDateTime> guests,
                                        List<LocalDateTime> participants) {
        var dates = new ArrayList<LocalDate>(DAILY_POINTS);
        for (int i = DAILY_POINTS - 1; i >= 0; i--) {
            dates.add(today.minusDays(i));
        }
        var roomCounts = countByDate(rooms);
        var userCounts = countByDate(users);
        var guestCounts = countByDate(guests);
        var participantCounts = countByDate(participants);

        var points = new ArrayList<TrendPoint>(DAILY_POINTS);
        for (var date : dates) {
            points.add(new TrendPoint(
                    date.getMonthValue() + "/" + date.getDayOfMonth(),
                    roomCounts.getOrDefault(date, 0L),
                    userCounts.getOrDefault(date, 0L),
                    guestCounts.getOrDefault(date, 0L),
                    participantCounts.getOrDefault(date, 0L)
            ));
        }
        return points;
    }

    private List<TrendPoint> buildMonthly(YearMonth currentMonth,
                                          List<LocalDateTime> rooms,
                                          List<LocalDateTime> users,
                                          List<LocalDateTime> guests,
                                          List<LocalDateTime> participants) {
        var months = new ArrayList<YearMonth>(MONTHLY_POINTS);
        for (int i = MONTHLY_POINTS - 1; i >= 0; i--) {
            months.add(currentMonth.minusMonths(i));
        }
        var roomCounts = countByMonth(rooms);
        var userCounts = countByMonth(users);
        var guestCounts = countByMonth(guests);
        var participantCounts = countByMonth(participants);

        var points = new ArrayList<TrendPoint>(MONTHLY_POINTS);
        for (var month : months) {
            points.add(new TrendPoint(
                    month.getMonthValue() + "월",
                    roomCounts.getOrDefault(month, 0L),
                    userCounts.getOrDefault(month, 0L),
                    guestCounts.getOrDefault(month, 0L),
                    participantCounts.getOrDefault(month, 0L)
            ));
        }
        return points;
    }

    private Map<LocalDate, Long> countByDate(List<LocalDateTime> createdAts) {
        var counts = new LinkedHashMap<LocalDate, Long>();
        for (var createdAt : createdAts) {
            if (createdAt == null) {
                continue;
            }
            counts.merge(createdAt.toLocalDate(), 1L, Long::sum);
        }
        return counts;
    }

    private Map<YearMonth, Long> countByMonth(List<LocalDateTime> createdAts) {
        var counts = new LinkedHashMap<YearMonth, Long>();
        for (var createdAt : createdAts) {
            if (createdAt == null) {
                continue;
            }
            counts.merge(YearMonth.from(createdAt), 1L, Long::sum);
        }
        return counts;
    }
}
