package com.dnd.modutime.core.timeblock.integration;

import com.dnd.modutime.annotations.SpringBootTestWithoutOAuthConfig;
import com.dnd.modutime.core.timeblock.application.TimeBlockService;
import com.dnd.modutime.core.timeblock.application.TimeReplaceValidator;
import com.dnd.modutime.core.timeblock.application.command.TimeReplaceCommand;
import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import com.dnd.modutime.core.timeblock.repository.AvailableDateTimeRepository;
import com.dnd.modutime.core.timeblock.repository.TimeBlockRepository;
import com.dnd.modutime.util.IntegrationSupporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._14_00;
import static com.dnd.modutime.fixture.TimeFixture._15_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_11;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

/**
 * 동일 (roomUuid, participantName) 에 대한 동시 replaceV1 호출이
 * StaleStateException(ObjectOptimisticLockingFailureException) 없이 처리되는지 검증.
 *
 * 회귀 방지 대상: MODUTIME-8 — derived deleteAllByTimeBlockId 의 ID 단위 DELETE 가
 * 두 트랜잭션에서 동일 행을 삭제하려 할 때 row count 0 으로 예외가 났던 이슈.
 *
 * 클래스 레벨 @Transactional 을 두지 않고 각 스레드가 독립 트랜잭션을 갖도록 한다.
 */
@SpringBootTestWithoutOAuthConfig
class TimeBlockServiceConcurrencyTest extends IntegrationSupporter {

    @Autowired
    private TimeBlockService timeBlockService;

    @Autowired
    private TimeBlockRepository timeBlockRepository;

    @Autowired
    private AvailableDateTimeRepository availableDateTimeRepository;

    @MockBean
    private TimeReplaceValidator timeReplaceValidator;

    @BeforeEach
    void setUp() {
        doNothing().when(timeReplaceValidator).validate(any(), any());
    }

    @AfterEach
    void cleanUp() {
        availableDateTimeRepository.deleteAll();
        timeBlockRepository.deleteAll();
    }

    @DisplayName("동일 TimeBlock 에 동시 replaceV1 두 번 들어와도 StaleStateException 없이 처리된다")
    @Test
    void 동일_타임블록에_동시_replace_요청이_들어와도_예외없이_처리된다() throws InterruptedException {
        // given — 기존 시간이 등록된 TimeBlock
        var participantName = "참여자1";
        var savedTimeBlock = timeBlockRepository.save(new TimeBlock(ROOM_UUID, participantName));
        var initialCommand = TimeReplaceCommand.of(ROOM_UUID, participantName, true,
                List.of(LocalDateTime.of(_2023_02_09, _12_00), LocalDateTime.of(_2023_02_09, _13_00)));
        timeBlockService.replaceV1(initialCommand);

        var commandA = TimeReplaceCommand.of(ROOM_UUID, participantName, true,
                List.of(LocalDateTime.of(_2023_02_10, _12_00), LocalDateTime.of(_2023_02_10, _13_00)));
        var commandB = TimeReplaceCommand.of(ROOM_UUID, participantName, true,
                List.of(LocalDateTime.of(_2023_02_11, _14_00), LocalDateTime.of(_2023_02_11, _15_00)));

        var startLatch = new CountDownLatch(1);
        var doneLatch = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        var errorA = new AtomicReference<Throwable>();
        var errorB = new AtomicReference<Throwable>();

        // when — 두 스레드가 동시에 replaceV1 호출
        executor.submit(() -> {
            try {
                startLatch.await();
                timeBlockService.replaceV1(commandA);
            } catch (Throwable t) {
                errorA.set(t);
            } finally {
                doneLatch.countDown();
            }
        });
        executor.submit(() -> {
            try {
                startLatch.await();
                timeBlockService.replaceV1(commandB);
            } catch (Throwable t) {
                errorB.set(t);
            } finally {
                doneLatch.countDown();
            }
        });

        startLatch.countDown();
        boolean finished = doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdownNow();

        // then — 둘 다 예외 없이 종료
        assertThat(finished).isTrue();
        assertThat(errorA.get()).isNull();
        assertThat(errorB.get()).isNull();

        // 최종 DB 상태는 A 또는 B 결과와 일치 (last write wins)
        List<AvailableDateTime> finalDateTimes = availableDateTimeRepository.findByTimeBlockId(savedTimeBlock.getId());
        assertThat(finalDateTimes).isNotEmpty();
        List<LocalDate> finalDates = finalDateTimes.stream()
                .map(AvailableDateTime::getDate)
                .distinct()
                .collect(Collectors.toList());
        assertThat(finalDates).hasSize(1);
        assertThat(finalDates.get(0)).isIn(_2023_02_10, _2023_02_11);
    }
}
